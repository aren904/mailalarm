package cn.infocore.main;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import cn.infocore.operator.Header;
import cn.infocore.protobuf.StmStreamerDrManage.GetServerInfoReturn;

public class DealSocket implements Runnable{
	private static final Logger logger=Logger.getLogger(DealSocket.class);
	private Socket socket;

	public DealSocket(Socket socket) {
		this.socket=socket;
	}
	
	
	public void run() {
		int ioret;
		InputStream in=null;
		OutputStream out=null;
		try {
			in=this.socket.getInputStream();
			out=this.socket.getOutputStream();
			byte[] h=new byte[Header.STREAMER_HEADER_LENGTH];
			ioret=in.read(h,0,Header.STREAMER_HEADER_LENGTH);
			if (ioret!=Header.STREAMER_HEADER_LENGTH) {
				logger.error(fmt("Failed to recived header,[%d] byte(s) expected,but [%d] is recevied.",Header.STREAMER_HEADER_LENGTH,ioret));
				return;
			}
			Header header=new Header();
			header.parseByteArray(h);
			if (header.getCommand()!=87000) {
				logger.error(fmt("Incorrect command"));
				return;
			}
			
			byte[] buffer=new byte[header.getDataLength()];
			ioret=in.read(buffer, 0, buffer.length);
			if (ioret!=buffer.length) {
				logger.error(fmt("Failed to receive Protobuf"));
				return;
			}
			logger.info("Received heartbeat from data_ark.");
			GetServerInfoReturn hrt=GetServerInfoReturn.parseFrom(buffer);
			//转化protobuf,放入阻塞队列
			CachedQueue.getInstance().addIntoQueue(hrt);
			header.setErrorCode(0);
			byte[] resp=header.toByteArray();
			out.write(resp, 0, resp.length);
			logger.info("Response heartbeat successed..");
			
		} catch (Exception e) {
			// TODO: handle exception
		}finally {
			try {
				if (in!=null)
					in.close();
				if (out!=null)
					out.close();
				this.socket.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}
	private static String fmt(String fmt,Object...obj) {
		return String.format(fmt, obj);
	}
}
