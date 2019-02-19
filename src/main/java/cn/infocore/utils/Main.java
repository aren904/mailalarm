package cn.infocore.utils;

import cn.infocore.operator.InforHeader;

public class Main {
	public static void main(String[] args) {
		InforHeader inforHeader=new InforHeader();
		inforHeader.setVersion((byte)1);
		inforHeader.setDataType((byte)0);
		inforHeader.setDirection((short)0);
		inforHeader.setFlags((short)0);
		inforHeader.setCommand(504);
		inforHeader.setErrorCode(100000);
		inforHeader.setDataLength(100);
		inforHeader.setFlags1(10000);
		
		byte[] parse=inforHeader.toByteArray();
		/*for (byte b:parse) {
			System.out.println(Byte.toString(b));
		}*/
		
		InforHeader inforHeader2=new InforHeader();
		inforHeader2.parseByteArray(parse);
		System.out.println(inforHeader2.getVersion());
		System.out.println(inforHeader2.getDataType());
		System.err.println(inforHeader2.getDirection());
		System.out.println(inforHeader2.getFlags());
		System.out.println(inforHeader2.getCommand());
		System.out.println(inforHeader2.getErrorCode());
		System.out.println(inforHeader2.getDataLength());
		System.out.println(inforHeader2.getFlags1());
	}
}
