package cn.infocore.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import cn.infocore.operator.Header;
import cn.infocore.protobuf.CloudManagerAlarm.AddDataArkRequest;
import cn.infocore.protobuf.CloudManagerAlarm.RemoveDataArkRequest;

public class DealInformation implements Runnable{
	
	private InputStream in;
	private OutputStream out;
	private Socket socket;
	
	public DealInformation(Socket socket) {
		this.socket=socket;
		this.in=null;
		this.out=null;
	}
	
	
	public void run() {
		int ioret;
		try {
			in=socket.getInputStream();
			out=socket.getOutputStream();
			byte[] header=new byte[Header.STREAMER_HEADER_LENGTH];
			ioret=this.in.read(header,0,Header.STREAMER_HEADER_LENGTH);
			if (ioret!=Header.STREAMER_HEADER_LENGTH) {
				//TODO
			}else {
				Header myHeader=new Header();
				myHeader.parseByteArray(header);
				int opCode=myHeader.getCommand();
				if (opCode==205) {
					
				}
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	//添加数据方舟
	private void addDataArk(Header header) throws IOException {
		if (header==null) {
			return;
		}
		if (header.getCommand()!=205) {
			return;
		}
		int ioret;
		byte[] buffer=new byte[header.getDataLength()];
		ioret=in.read(buffer, 0, buffer.length);
		if (ioret!=buffer.length) {
			return ;
		}
		AddDataArkRequest request=AddDataArkRequest.parseFrom(buffer);
		if (request==null) {
			return ;
		}
		String uuid=request.getId();
		DataArkList.getInstance().addDataArk(uuid);
		header.setErrorCode(0);
	}
	
	//删除数据方舟
	private void removeDataArk(Header header) throws IOException{
		if (header==null) {
			return;
		}
		if (header.getCommand()!=206) {
			return ;
		}
		int ioret;
		byte[] buffer=new byte[header.getDataLength()];
		ioret=in.read(buffer, 0, buffer.length);
		if (ioret!=buffer.length) {
			return ;
		}
		RemoveDataArkRequest request=RemoveDataArkRequest.parseFrom(buffer);
		if (request==null) {
			return ;
		}
		String uuid=request.getId();
		DataArkList.getInstance().removeDataArk(uuid);
		header.setErrorCode(0);
	}
	
	//添加客户端
	private void addClient(Header header) {
		
	}
	//删除客户端
	private void removeClient(Header header) {
		
	}
	//更新邮件报警配置
	private void updateEmailAlarm(Header header) {
		
	}
	//测试邮件报警配置
	private void verifyEmailAlarm(Header header) {
		
	}
}
