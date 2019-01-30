package cn.infocore.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import cn.infocore.operator.Header;
import cn.infocore.protobuf.CloudManagerAlarm.AddDataArkRequest;
import cn.infocore.protobuf.CloudManagerAlarm.RemoveDataArkRequest;
import cn.infocore.utils.DBUtils;
import cn.infocore.utils.MyDataSource;

public class DealInformation implements Runnable{
	private static final Logger logger=Logger.getLogger(DealInformation.class);
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
	private void addDataArk(Header header) throws Exception {
		logger.info("Recived addDataArk command.");
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
		logger.info("Need to add data ark id:"+uuid);
		String ip="";
		String sql="select ip from data_ark where id=?";
		String[] param= {uuid};
		ResultSet rSet=DBUtils.executQuery(MyDataSource.getConnection(), sql, param);
		if (rSet.next()) {
			ip=rSet.getString("ip");
		}
		logger.info("Need to add data ark ip:"+ip);
		DataArkList.getInstance().addDataArk(uuid,ip);
		logger.info("Add data ark successed.");
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
	
	//更新邮件报警配置
	private void updateEmailAlarm(Header header) {
		
	}
	//测试邮件报警配置
	private void verifyEmailAlarm(Header header) {
		
	}
}
