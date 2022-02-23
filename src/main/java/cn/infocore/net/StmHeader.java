package cn.infocore.net;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import org.apache.log4j.Logger;

import lombok.Data;

/**
 * osnstm服务交互头信息：与C交互需要大小头转换
 */
@Data
public class StmHeader {
	
	private static final Logger logger = Logger.getLogger(StmHeader.class);
	
	public static final byte STREAMER_VERSION_CODE = 1;
	
    public static final int STREAMER_HEADER_LENGTH = 16;
    
    /**
	 * 消息头:长度16
	 * Message header
	 *      offset    length    type    description
	 *           0         1    byte    version [1]
	 *           1         1    byte    The data type of the message body [2]
	 *           2         2    int16   Error code, see also enum StmRetStatus
	 *           4         2    int16   no use
	 *           6         2    int16   Request from [3]
	 *           12        4    int32   Command, see also enum StmCommand
	 *           16        4    int32   The data length of the message body
	 *  [1] osnstm -> mailalarm : 1
	 *      mailalarm -> osnstm  : 1
	 *  [2] 0: ST_MSG_XML, // xml
        	1: ST_MSG_BINARY_STREAM, //流
        	2: ST_MSG_PROTOBUF, //protobuf
	 *  [3] osnstm -> mailalarm : 0
	 *      mailalarm -> osnstm  : 25
	 */
    private byte version;
    private byte dataType;
    private StmRetStatus errorCode;
    private short flags;  //暂未用到，默认值传0
    private short from;
    private StmCommand command;
    private int dataLength;

    /**
     * Header小端转大端：stm转mailheader
     * @param ba
     * @return
     */
    public boolean parseByteArray (byte[] ba) {
        if (ba == null) {
            logger.error("StmHeader byte[] is null");
            return false;
        }
        
        if (ba.length != STREAMER_HEADER_LENGTH) {
            logger.error("StmHeader Length is wrong");
            return false;
        }

        byte[] baVersion = Arrays.copyOfRange(ba, 0, 1);
        this.version = ByteBuffer.wrap(baVersion).order(ByteOrder.LITTLE_ENDIAN).get();

        byte[] baDataType = Arrays.copyOfRange(ba, 1, 2);
        this.dataType = ByteBuffer.wrap(baDataType).order(ByteOrder.LITTLE_ENDIAN).get();

        byte[] baErrorCode = Arrays.copyOfRange(ba, 2, 4);
        this.errorCode =StmRetStatus.getRetStatus(ByteBuffer.wrap(baErrorCode).order(ByteOrder.LITTLE_ENDIAN).getShort());

        byte[] baFlags = Arrays.copyOfRange(ba, 4, 6);
        this.flags = ByteBuffer.wrap(baFlags).order(ByteOrder.LITTLE_ENDIAN).getShort();
        
        byte[] baFrom = Arrays.copyOfRange(ba, 6, 8);
        this.from = ByteBuffer.wrap(baFrom).order(ByteOrder.LITTLE_ENDIAN).getShort();

        byte[] baCommand = Arrays.copyOfRange(ba, 8, 12);
        this.command = StmCommand.getCommandCode(ByteBuffer.wrap(baCommand).order(ByteOrder.LITTLE_ENDIAN).getInt());

        byte[] baLength = Arrays.copyOfRange(ba, 12, 16);
        this.dataLength = ByteBuffer.wrap(baLength).order(ByteOrder.LITTLE_ENDIAN).getInt();

        logMe();
        return true;
    }

    /**
     * Header大端转小端：mailalarm到osnstm
     * @return
     */
    public byte[] toByteArray () {
        byte[] header = new byte[STREAMER_HEADER_LENGTH];

        byte[] baVersion = ByteBuffer.allocate(1).put(STREAMER_VERSION_CODE).array();
        System.arraycopy(baVersion, 0, header, 0, 1);

        byte[] baDatatype=ByteBuffer.allocate(1).put(this.dataType).array();
        System.arraycopy(baDatatype, 0, header, 1, 1);
        
        byte[] baErrorCode = ByteBuffer.allocate(2).putShort(this.errorCode.getShort()).array();
        System.arraycopy(baErrorCode, 0, header, 2, 2);

        byte[] baFlags=ByteBuffer.allocate(2).putShort(this.flags).array();
        System.arraycopy(baFlags, 0, header, 4, 2);
        
        byte[] baFrom=ByteBuffer.allocate(2).putShort(this.from).array();
        System.arraycopy(baFrom, 0, header, 6, 2);

        byte[] baCommand=ByteBuffer.allocate(4).putInt(this.command.getValue()).array();
        System.arraycopy(baCommand, 0, header, 8, 4);

        byte[] baLength = ByteBuffer.allocate(4).putInt(this.dataLength).array();
        System.arraycopy(baLength, 0, header, 12, 4);

        return header;
    }
    
    public byte[] toByteArrayLittle () {
    	byte[] header = new byte[STREAMER_HEADER_LENGTH];

        byte[] baVersion = ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN).put(STREAMER_VERSION_CODE).array();
        System.arraycopy(baVersion, 0, header, 0, 1);

        byte[] baDatatype=ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN).put(this.dataType).array();
        System.arraycopy(baDatatype, 0, header, 1, 1);
        
        byte[] baErrorCode = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(this.errorCode.getShort()).array();
        System.arraycopy(baErrorCode, 0, header, 2, 2);

        byte[] baFlags=ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(this.flags).array();
        System.arraycopy(baFlags, 0, header, 4, 2);
        
        byte[] baFrom=ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(this.from).array();
        System.arraycopy(baFrom, 0, header, 6, 2);

        byte[] baCommand=ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(this.command.getValue()).array();
        System.arraycopy(baCommand, 0, header, 8, 4);

        byte[] baLength = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(this.dataLength).array();
        System.arraycopy(baLength, 0, header, 12, 4);

        return header;
    }

    public void logMe () {
        logger.debug(String.format("[version]:%s [dataType]:%s [ErrorCode]:%s [Flags]:%s [from]:%s [Command]:%s [Data length]:%s",
                (int)this.version,
                (int)this.dataType,
                this.errorCode,
                this.flags,
                this.from,
                this.command,
                this.dataLength
        ));
    }
}
