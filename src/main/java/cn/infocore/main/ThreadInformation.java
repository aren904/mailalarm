package cn.infocore.main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import cn.infocore.entity.Email_alarm;
import cn.infocore.mail.MailCenterRestry;
import cn.infocore.utils.DBUtils;
import cn.infocore.utils.MyDataSource;


//接收来自强哥的通知
public class ThreadInformation extends Thread{
	private static final Logger logger=Logger.getLogger(ThreadInformation.class);
	private static volatile ThreadInformation instance=null;
	private static final int C_PORT=23334;
	private ExecutorService pool;
	/*private Connection connection;
	private MailCenterRestry center;
	private DataArkList dataArkList;*/
	
	private ThreadInformation() {
		/*connection=MyDataSource.getConnection();
		center=MailCenterRestry.getInstance();
		dataArkList=DataArkList.getInstance();*/
		pool=Executors.newCachedThreadPool();
	}
	
	public static ThreadInformation getInstance() {
		if (instance==null) {
			synchronized (ThreadInformation.class) {
				if (instance==null) {
					instance=new ThreadInformation();
				}
			}
		}
		
		return instance;
	}
	
	//测试邮件通过把config直接传递过来，不需要读取数据库
	
	
	
	
	public void run() {
		/*String sql="select * from email_alarm";
		ResultSet rs=DBUtils.executQuery(connection, sql, null);
		List<Email_alarm> list=new LinkedList<Email_alarm>();
		try {
			while (rs.next()) {
				Email_alarm email=new Email_alarm();
				email.setId(rs.getInt("id"));
				email.setEnabled(rs.getByte("enabled"));
				email.setExcept(rs.getString("exceptions"));
				email.setLimit_enabled(rs.getByte("limit_enabled"));
				email.setLimit_suppress_time(rs.getLong("limit_suppress_time"));
				email.setSender_email(rs.getString("sender_email"));
				email.setSender_password(rs.getString("sender_password"));
				email.setSmtp_address(rs.getString("smtp_address"));
				email.setSmtp_port(rs.getInt("smtp_port"));
				email.setSsl_encrypt(rs.getByte("ssl_encrypt"));
				email.setReceiver_emails(rs.getString("receiver_emails"));
				email.setUser_id(rs.getString("user_id"));
				list.add(email);
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (list.size()>0) {
			center.addAllMailService(list);
		}*/
		//MyDataSource.close(connection);
		ServerSocket server=null;
		try {
			server=new ServerSocket(C_PORT);
			logger.info("ThreadInformation start.....");
			while(true) {
				Socket socket=server.accept();
				pool.execute(new DealInformation(socket));
			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally {
			try {
				server.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
		/*
		MailCenterRestry center=MailCenterRestry.getInstance();
		//0.如果是第一次，从数据库中读取配置，保存在MailCenterRestry的map内存中
		//如果为空，则阻塞，等待强哥通知
		
		//数据方舟的添加，移除
		
		//1.如果收到添加操作
		center.addMailService(name, sender);
		
		//2.如果收到删除操作
		center.deleteMailService(name);
		
		//3.如果收到更新操作
		center.updateMailService(name, sender);
		
		//4.如果收到发送测试邮件操作,自己构造，发送测试邮件
		new MailSender(config).send(data);*/
	}

	
}
