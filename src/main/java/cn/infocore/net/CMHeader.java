package cn.infocore.net;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import org.apache.log4j.Logger;

import lombok.Data;

/**
 * 与管理平台交互头信息
 */
@Data
public class CMHeader {
	
	private static final Logger logger=Logger.getLogger(CMHeader.class);
	
	public static final byte CM_HEADER_VERSION=1;
	
	public static final int CM_HEADER_LENGTH=24;
	
	/**
	 * 消息头:长度24
	 * Message header
	 *      offset    length    type    description
	 *           0         1    byte    version [1]
	 *           1         1    byte    The data type of the message body [2]
	 *           2         2    int16   Request from [3]
	 *           4         4    int32   Command, see also enum CMCommand
	 *           8         4    int32   Error code, see also enum CMErrorCode
	 *           12        4    int32   The data length of the message body
	 *           16        8    int32   flags1 no use
	 *  [1] cloud -> mailalarm : 1
	 *      mailalarm -> cloud  : 1
	 *  [2] 0: ST_MSG_XML, // xml
        	1: ST_MSG_BINARY_STREAM, //流
        	2: ST_MSG_PROTOBUF, //protobuf
	 *  [3] cloud -> mailalarm : 0
	 *      mailalarm -> cloud  : 25
	 */
	
	private byte version;
	
	private byte dataType;
	
	private short from;
	
	private CMCommand command;
	
	private CMRetStatus errorCode;
	
	private int dataLength;
	
	private long flags1;
	
	public boolean parseByteArray(byte[] ba) {
		if (ba==null) {
			logger.error("CMHeader byte[] is null");
			return false;
		}
		
		if (ba.length!=CM_HEADER_LENGTH) {
			logger.error("CMHeader length is wrong");
			return false;
		}
		
		byte[] baVersion=Arrays.copyOfRange(ba, 0, 1);
		this.version=ByteBuffer.wrap(baVersion).order(ByteOrder.LITTLE_ENDIAN).get();
		
		byte[] baMsgType=Arrays.copyOfRange(ba, 1, 2);
		this.dataType=ByteBuffer.wrap(baMsgType).order(ByteOrder.LITTLE_ENDIAN).get();
		
		byte[] baFrom=Arrays.copyOfRange(ba, 2, 4);
		this.from=ByteBuffer.wrap(baFrom).order(ByteOrder.LITTLE_ENDIAN).getShort();
		
		byte[] baCommand=Arrays.copyOfRange(ba, 4, 8);
		this.command=CMCommand.getCommandCode(ByteBuffer.wrap(baCommand).order(ByteOrder.LITTLE_ENDIAN).getInt());
		
		byte[] baErrorCode=Arrays.copyOfRange(ba, 8, 12);
		this.errorCode=CMRetStatus.getRetStatus(ByteBuffer.wrap(baErrorCode).order(ByteOrder.LITTLE_ENDIAN).getInt());
		
		byte[] baLength = Arrays.copyOfRange(ba, 12, 16);
		this.dataLength = ByteBuffer.wrap(baLength).order(ByteOrder.LITTLE_ENDIAN).getInt();
		
		byte[] baFlags1 = Arrays.copyOfRange(ba, 16, 24);
		this.flags1 = ByteBuffer.wrap(baFlags1).order(ByteOrder.LITTLE_ENDIAN).getLong();
		
		logMe();
		return true;
	}
	
	public byte[] toByteArray () {
		
		byte[] header = new byte[CM_HEADER_LENGTH];
	
		byte[] baVersion = ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN).put(CM_HEADER_VERSION).array();
		System.arraycopy(baVersion, 0, header, 0, 1);
		
		byte[] baMsgType = ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN).put(this.dataType).array();
		System.arraycopy(baMsgType, 0, header, 1, 1);

		byte[] baFrom=ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(this.from).array();
		System.arraycopy(baFrom, 0, header,2, 2);
		
		byte[] baCommand=ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(this.command.getValue()).array();
		System.arraycopy(baCommand, 0, header, 4, 4);
		
		byte[] baerrorCode=ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(this.errorCode.getValue()).array();
		System.arraycopy(baerrorCode, 0, header, 8, 4);
		
		byte[] baLength=ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(0).array();
		System.arraycopy(baLength, 0, header, 12, 4);
		
		byte[] baFlags1=ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(this.flags1).array();
		System.arraycopy(baFlags1, 0, header, 16, 8);
		
		logMe();
		return header;
	}
	
	public void logMe () {
		logger.debug("----------CMHeader------------ ");
		logger.debug("[version]: " + (int) this.version);
		logger.debug("[dataType]: " +  (int)this.dataType);
		logger.debug("[From]:"+this.from);
		logger.debug("[Command]: " + this.command);
		logger.debug("[ErrorCode]: " + this.errorCode);
		logger.debug("[Data length]: " + this.dataLength);
		logger.debug("[Flags1]:"+this.flags1);
	}

}
