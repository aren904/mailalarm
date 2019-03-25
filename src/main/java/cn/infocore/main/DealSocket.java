package cn.infocore.main;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import cn.infocore.protobuf.StmStreamerDrManage;
import org.apache.log4j.Logger;
import cn.infocore.operator.Header;
import cn.infocore.protobuf.StmStreamerDrManage.GetServerInfoReturn;
import cn.infocore.utils.Utils;

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
				logger.error(Utils.fmt("Failed to recived header,[%d] byte(s) expected,but [%d] is recevied.",Header.STREAMER_HEADER_LENGTH,ioret));
				throw new Exception();
			}
			Header header=new Header();
			header.parseByteArray(h);
			if (header.getCommand()!=87000) {
				logger.error(Utils.fmt("Incorrect command"));
				throw new Exception();
			}
			
			byte[] buffer=new byte[header.getDataLength()];
			ioret=in.read(buffer, 0, buffer.length);
			if (ioret!=buffer.length) {
				logger.error(Utils.fmt("Failed to receive Protobuf"));
				throw new Exception();
			}
			logger.info("Received heartbeat from data_ark.");
/*			GetServerInfoReturn hrt=GetServerInfoReturn.parseFrom(buffer);*/
			//转化protobuf,放入阻塞队列
			//CachedQueue.getInstance().addIntoQueue(GetServerInfoReturn.parseFrom(buffer.clone()));
			header.setErrorCode(0);
			byte[] resp=header.toByteArray();
			out.write(resp, 0, resp.length);
			out.flush();
			GetServerInfoReturn hrt=GetServerInfoReturn.parseFrom(buffer);
			new InfoProcessData(hrt).run();
			hrt.toBuilder().clear();
			hrt.toBuilder().clearClients();
			hrt.toBuilder().clearServer();
			hrt.toBuilder().clearUuid();
			hrt.toBuilder().clearVcents();
			logger.info("Response heartbeat successed..");
		} catch (Exception e) {
			logger.error("DealSocket failed."+e);
		}finally {
			try {
				if (in!=null)
					in.close();
				if (out!=null)
					out.close();
				this.socket.close();
			} catch (Exception e2) {
				logger.error(e2);
			}
		}
	}
	
}
