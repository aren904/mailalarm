package cn.infocore.operator;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import org.apache.log4j.Logger;



public class Header {
	//public static final short STREAMER_FROM_VMWARE = 9;
	public static final byte STREAMER_VERSION_CODE = 1;
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
		logger.info("[version]: " + (int) this.version);
		logger.info("[dataType]: " +  (int)this.dataType);
		logger.info("[direction]:"+this.direction);
		logger.info("[Flags]: " + this.flags);
		logger.info("[Command]: " + this.command);
		logger.info("[ErrorCode]: " + this.errorCode);
		logger.info("[Data length]: " + this.dataLength);
	}
	


	public void setVersion(byte version) {
		this.version = version;
	}



	public short getVersion() {
		return version;
	}



	public void setVersion(short version) {
		this.version = version;
	}



	public short getDataType() {
		return dataType;
	}



	public void setDataType(short dataType) {
		this.dataType = dataType;
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


	/*public boolean parseByteArray (byte[] ba) {
		if (ba == null) {
			logger.info("byte[] is null");
			return false;
		}
		if (ba.length != STREAMER_HEADER_LENGTH) {
			logger.info("Length is wrong");
			return false;
		}
		
		byte[] baVersion = Arrays.copyOfRange(ba, 0, 1);
		this.version = ByteBuffer.wrap(baVersion).order(ByteOrder.BIG_ENDIAN).get();
		
		byte[] baMsgType = Arrays.copyOfRange(ba, 1, 2);
		this.dataType = ByteBuffer.wrap(baMsgType).order(ByteOrder.BIG_ENDIAN).get();
		
		byte[] baReturnStatus = Arrays.copyOfRange(ba, 2, 4);
		this.direction =ByteBuffer.wrap(baReturnStatus).order(ByteOrder.BIG_ENDIAN).getShort(); 
		
		byte[] baFlags = Arrays.copyOfRange(ba, 4, 6);
		this.flags = ByteBuffer.wrap(baFlags).order(ByteOrder.BIG_ENDIAN).getShort();
		
		byte[] baFrom = Arrays.copyOfRange(ba, 6, 10);
		this.command = ByteBuffer.wrap(baFrom).order(ByteOrder.BIG_ENDIAN).getInt();
		
		byte[] baOperation = Arrays.copyOfRange(ba, 10, 14);
		this.errorCode = ByteBuffer.wrap(baOperation).order(ByteOrder.BIG_ENDIAN).getInt(); 
		
		byte[] baLength = Arrays.copyOfRange(ba, 14, 18);
		this.dataLength = ByteBuffer.wrap(baLength).order(ByteOrder.BIG_ENDIAN).getInt();
		
		logger.info("Create Header by big endian  parsing binary array.");
		logMe();
		return true;
	}*/
	
	
	public boolean parseByteArray (byte[] ba) {
		if (ba == null) {
			logger.info("byte[] is null");
			return false;
		}
		if (ba.length != STREAMER_HEADER_LENGTH) {
			logger.info("Length is wrong");
			return false;
		}
		
		byte[] baVersion = Arrays.copyOfRange(ba, 0, 2);
		this.version = ByteBuffer.wrap(baVersion).order(ByteOrder.LITTLE_ENDIAN).getShort();
		
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
	
	

	/*public byte[] toByteArray () {
		
		byte[] header = new byte[STREAMER_HEADER_LENGTH];
		
		byte[] baVersion = ByteBuffer.allocate(2)
				.put(STREAMER_VERSION_CODE).array();
		System.arraycopy(baVersion, 0, header, 0, 2);
		
		byte[] baMsgType = ByteBuffer.allocate(2)
				.put(this.dataType).array();
		System.arraycopy(baMsgType, 0, header, 1, 1);

		if (this.returnStatus != null) {
			byte[] baReturnStatus = ByteBuffer.allocate(2)
					.putShort(this.returnStatus.getShort()).array();
			System.arraycopy(baReturnStatus, 0, header, 2, 2);
		}

		byte[] baFlags = ByteBuffer.allocate(2)
				.putShort(this.flags).array();
		System.arraycopy(baFlags, 0, header, 4, 2);
		
		byte[] baFrom = ByteBuffer.allocate(2)
				.putInt(this.command).array();
		System.arraycopy(baFrom, 0, header, 6, 4);
		
		if (this.operation != null) {
			byte[] baOperation = ByteBuffer.allocate(4)
					.putInt(this.operation.getValue()).array();
			System.arraycopy(baOperation, 0, header, 8, 4);
		}

		byte[] baLength = ByteBuffer.allocate(4)
				.putInt(this.dataLength).array();
		System.arraycopy(baLength, 0, header, 14, 4);
		
		//logger.debug("StreamerHeader dumped to binary array.");
		return header;
	}*/
}
