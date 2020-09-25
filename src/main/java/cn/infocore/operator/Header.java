package cn.infocore.operator;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import lombok.Data;
import org.apache.log4j.Logger;

//streamer交互头信息
@Data
public class Header {
    public static final short STREAMER_VERSION_CODE = 1;
    public static final int STREAMER_HEADER_LENGTH = 20;
    private static final Logger logger = Logger.getLogger(Header.class);
    private short version;
    private short dataType;
    private short direction;
    private short flags;
    private int command;
    private int errorCode;
    private int dataLength;

    public void logMe () {
        logger.info(String.format("[version]:%s [dataType]:%s [direction]:%s [Flags]:%s [Command]:%s [ErrorCode]:%s [Data length]:%s",
                (int) this.version,
                (int)this.dataType,
                this.direction,
                this.flags,
                this.command,
                this.errorCode,
                this.dataLength
        ));
    }


    public boolean parseByteArray (byte[] ba) {
        if (ba == null) {
            logger.info("byte[] is null");
            return false;
        }
        if (ba.length != STREAMER_HEADER_LENGTH) {
            logger.info("Length is wrong");
            return false;
        }

		/*byte[] baVersion = Arrays.copyOfRange(ba, 0, 2);
		this.version = ByteBuffer.wrap(baVersion).order(ByteOrder.LITTLE_ENDIAN).getShort();*/
        byte[] baVersion = Arrays.copyOfRange(ba, 0, 1);
        this.version = ByteBuffer.wrap(baVersion).order(ByteOrder.LITTLE_ENDIAN).get();

        byte[] baMsgType = Arrays.copyOfRange(ba, 2, 4);
        this.dataType = ByteBuffer.wrap(baMsgType).order(ByteOrder.LITTLE_ENDIAN).getShort();

        byte[] baReturnStatus = Arrays.copyOfRange(ba, 4, 6);
        this.direction =ByteBuffer.wrap(baReturnStatus).order(ByteOrder.LITTLE_ENDIAN).getShort();

        byte[] baFlags = Arrays.copyOfRange(ba, 6, 8);
        this.flags = ByteBuffer.wrap(baFlags).order(ByteOrder.LITTLE_ENDIAN).getShort();

        byte[] baFrom = Arrays.copyOfRange(ba, 8, 12);
        this.command = ByteBuffer.wrap(baFrom).order(ByteOrder.LITTLE_ENDIAN).getInt();

        byte[] baOperation = Arrays.copyOfRange(ba, 12, 16);
        this.errorCode = ByteBuffer.wrap(baOperation).order(ByteOrder.LITTLE_ENDIAN).getInt();

        byte[] baLength = Arrays.copyOfRange(ba, 16, 20);
        this.dataLength = ByteBuffer.wrap(baLength).order(ByteOrder.LITTLE_ENDIAN).getInt();

        logger.info("Create Header by little endian parsing binary array.");
        logMe();
        return true;
    }

    public byte[] toByteArray () {
        byte[] header = new byte[STREAMER_HEADER_LENGTH];

        byte[] baVersion = ByteBuffer.allocate(2)
                .putShort(STREAMER_VERSION_CODE).array();
        System.arraycopy(baVersion, 0, header, 0, 2);

        byte[] baDatatype=ByteBuffer.allocate(2)
                .putShort(this.dataType).array();
        System.arraycopy(baDatatype, 0, header, 2, 2);

        byte[] baDirection=ByteBuffer.allocate(2)
                .putShort(this.direction).array();
        System.arraycopy(baDirection, 0, header, 4, 2);

        byte[] baFlags=ByteBuffer.allocate(2)
                .putShort(this.flags).array();
        System.arraycopy(baFlags, 0, header, 6, 2);

        byte[] baCommand=ByteBuffer.allocate(4)
                .putInt(this.command).array();
        System.arraycopy(baCommand, 0, header, 8, 4);

        byte[] baFrom = ByteBuffer.allocate(4)
                .putInt(this.errorCode).array();
        System.arraycopy(baFrom, 0, header, 12, 4);

        byte[] baLength = ByteBuffer.allocate(4)
                .putInt(0).array();
        System.arraycopy(baLength, 0, header, 16, 4);

        logger.debug("Header dumped to binary array.");
        return header;
    }

    /**
     * 临时测试：java
     *
     */
    public byte[] toByteArrayLittle () {
        byte[] header = new byte[STREAMER_HEADER_LENGTH];

        byte[] baVersion = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(STREAMER_VERSION_CODE).array();
        System.arraycopy(baVersion, 0, header, 0, 2);

        byte[] baDatatype=ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN)
                .putShort(this.dataType).array();
        System.arraycopy(baDatatype, 0, header, 2, 2);

        byte[] baDirection=ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN)
                .putShort(this.direction).array();
        System.arraycopy(baDirection, 0, header, 4, 2);

        byte[] baFlags=ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN)
                .putShort(this.flags).array();
        System.arraycopy(baFlags, 0, header, 6, 2);

        byte[] baCommand=ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                .putInt(this.command).array();
        System.arraycopy(baCommand, 0, header, 8, 4);

        byte[] baFrom = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                .putInt(this.errorCode).array();
        System.arraycopy(baFrom, 0, header, 12, 4);

        byte[] baLength = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                .putInt(this.dataLength).array();
        System.arraycopy(baLength, 0, header, 16, 4);

        logger.debug("Header dumped to binary array.");
        return header;
    }

}
