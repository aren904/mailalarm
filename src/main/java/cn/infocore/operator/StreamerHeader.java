package cn.infocore.operator;

import lombok.Data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Data
public class StreamerHeader {
    public static final short STREAMER_VERSION_CODE = 1;
    public static final int STREAMER_HEADER_LENGTH = 16;
    private short version;
    private short msgType;
    private short retStatus;
    private short flags;
    private short from;
    private int cmd;
    private int dataLength;


    public byte[] toByteArray () {
        byte[] header = new byte[16];

        byte[] baVersion = ByteBuffer.allocate(2)
                .putShort(STREAMER_VERSION_CODE).array();
        System.arraycopy(baVersion, 0, header, 0, 2);

        byte[] baMsgType=ByteBuffer.allocate(2)
                .putShort(this.msgType).array();
        System.arraycopy(baMsgType, 0, header, 2, 2);

        byte[] baRetStatus=ByteBuffer.allocate(2)
                .putShort(this.retStatus).array();
        System.arraycopy(baRetStatus, 0, header, 4, 2);

        byte[] baFlags=ByteBuffer.allocate(2)
                .putShort(this.flags).array();
        System.arraycopy(baFlags, 0, header, 6, 2);

        byte[] baFrom=ByteBuffer.allocate(4)
                .putShort(this.from).array();
        System.arraycopy(baFrom, 0, header, 8, 4);

        byte[] baCmd = ByteBuffer.allocate(4)
                .putInt(this.cmd).array();
        System.arraycopy(baCmd, 0, header, 12, 4);

        byte[] baLength = ByteBuffer.allocate(4)
                .putInt(0).array();
        System.arraycopy(baLength, 0, header, 16, 4);

       // logger.debug("Header dumped to binary array.");
        return header;
    }

    public byte[] toByteArrayLittle () {
        byte[] header = new byte[STREAMER_HEADER_LENGTH];

        byte[] baVersion = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(STREAMER_VERSION_CODE).array();
        System.arraycopy(baVersion, 0, header, 0, 1);

        byte[] baMsgType=ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN)
                .putShort(this.msgType).array();
        System.arraycopy(baMsgType, 0, header, 1, 1);

        byte[] baRetStatus=ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN)
                .putShort(this.retStatus).array();
        System.arraycopy(baRetStatus, 0, header, 2, 2);

        byte[] baFlags=ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN)
                .putShort(this.flags).array();
        System.arraycopy(baFlags, 0, header, 4, 2);

        byte[] baFrom=ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                .putShort(this.from).array();
        System.arraycopy(baFrom, 0, header, 6, 2);

        byte[] baCmd= ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                .putInt(this.cmd).array();
        System.arraycopy(baCmd, 0, header, 8, 4);

        byte[] baLength = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                .putInt(this.dataLength).array();
        System.arraycopy(baLength, 0, header, 12, 4);

       // logger.debug("Header dumped to binary array.");
        return header;
    }
}
