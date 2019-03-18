package cn.infocore.operator;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import org.apache.log4j.Logger;

public class InforHeader {
	private static final Logger logger=Logger.getLogger(InforHeader.class);
	public static final int INFOR_HEADER_LENGTH=26;
	public static final byte INFOR_HEADER_VERSION=1;
	private byte version;
	private byte dataType;
	private short direction;
	private short flags;
	private int command;
	private int errorCode;
	private int dataLength;
	private long flags1;
	
	public byte getVersion() {
		return version;
	}
	public void setVersion(byte version) {
		this.version = version;
	}
	public byte getDataType() {
		return dataType;
	}
	public void setDataType(byte dataType) {
		this.dataType = dataType;
	}
	public short getDirection() {
		return direction;
	}
	public void setDirection(short direction) {
		this.direction = direction;
	}
	public short getFlags() {
		return flags;
	}
	public void setFlags(short flags) {
		this.flags = flags;
	}
	public int getCommand() {
		return command;
	}
	public void setCommand(int command) {
		this.command = command;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public int getDataLength() {
		return dataLength;
	}
	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}
	public long getFlags1() {
		return flags1;
	}
	public void setFlags1(long flags1) {
		this.flags1 = flags1;
	}
	
	public void logMe () {
		logger.info("[version]: " + (int) this.version);
		logger.info("[dataType]: " +  (int)this.dataType);
		logger.info("[direction]:"+this.direction);
		logger.info("[Flags]: " + this.flags);
		logger.info("[Command]: " + this.command);
		logger.info("[ErrorCode]: " + this.errorCode);
		logger.info("[Data length]: " + this.dataLength);
		logger.info("[Flags1]:"+this.flags1);
	}
	
	public boolean parseByteArray(byte[] ba) {
		if (ba==null) {
			logger.info("byte[] is null");
			return false;
		}
		if (ba.length!=INFOR_HEADER_LENGTH) {
			logger.info("Length is wrong");
			return false;
		}
		byte[] baVersion=Arrays.copyOfRange(ba, 0, 1);
		this.version=ByteBuffer.wrap(baVersion).order(ByteOrder.LITTLE_ENDIAN).get();
		
		byte[] baMsgType=Arrays.copyOfRange(ba, 1, 2);
		this.dataType=ByteBuffer.wrap(baMsgType).order(ByteOrder.LITTLE_ENDIAN).get();
		
		byte[] baDirection=Arrays.copyOfRange(ba, 2, 4);
		this.direction=ByteBuffer.wrap(baDirection).order(ByteOrder.LITTLE_ENDIAN).getShort();
		
		byte[] baFlags=Arrays.copyOfRange(ba, 4, 6);
		this.flags=ByteBuffer.wrap(baFlags).order(ByteOrder.LITTLE_ENDIAN).getShort();
		
		
		byte[] baCommand=Arrays.copyOfRange(ba, 6, 10);
		this.command=ByteBuffer.wrap(baCommand).order(ByteOrder.LITTLE_ENDIAN).getInt();
		
		
		byte[] baerrorCode=Arrays.copyOfRange(ba, 10, 14);
		this.errorCode=ByteBuffer.wrap(baerrorCode).order(ByteOrder.LITTLE_ENDIAN).getInt();
		
		
		
		byte[] baLength = Arrays.copyOfRange(ba, 14, 18);
		this.dataLength = ByteBuffer.wrap(baLength).order(ByteOrder.LITTLE_ENDIAN).getInt();
		
	
		
		byte[] baFlags1 = Arrays.copyOfRange(ba, 18, 26);
		this.flags1 = ByteBuffer.wrap(baFlags1).order(ByteOrder.LITTLE_ENDIAN).getLong();
		
	
		logger.info("Create Info Header by little endian parsing binary array.");
		logMe();
		return true;
	}
	
public byte[] toByteArray () {
		
		byte[] header = new byte[INFOR_HEADER_LENGTH];
	
		byte[] baVersion = ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN)
				.put(INFOR_HEADER_VERSION).array();
		System.arraycopy(baVersion, 0, header, 0, 1);
		
		byte[] baMsgType = ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN)
				.put(this.dataType).array();
		System.arraycopy(baMsgType, 0, header, 1, 1);

		byte[] baDirection=ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN)
				.putShort(this.direction).array();
		System.arraycopy(baDirection, 0, header, 2, 2);
		
		byte[] baFlags=ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN)
				.putShort(this.flags).array();
		System.arraycopy(baFlags, 0, header, 4, 2);
		
		byte[] baCommand=ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
				.putInt(this.command).array();
		System.arraycopy(baCommand, 0, header, 6, 4);
		
		
		byte[] baerrorCode=ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
				.putInt(this.errorCode).array();
		System.arraycopy(baerrorCode, 0, header, 10, 4);
		
		byte[] baLength=ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
				.putInt(0).array();
		System.arraycopy(baLength, 0, header, 14, 4);
		
		
		byte[] baFlags1=ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN)
				.putLong(this.flags1).array();
		System.arraycopy(baFlags1, 0, header, 18, 8);
		
		logger.info("InforHeader dumped to binary array.");
		logMe();
		return header;
	}
	

}
