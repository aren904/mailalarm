package cn.infocore.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import StmStreamerDrManage.StreamerClouddrmanage;
import cn.infocore.operator.Header;
//import cn.infocore.protobuf.StmStreamerDrManage.Client;
//import cn.infocore.protobuf.StmStreamerDrManage.ClientType;
//import cn.infocore.protobuf.StmStreamerDrManage.FaultType;
//import cn.infocore.protobuf.StmStreamerDrManage.GetServerInfoReturn;
//import cn.infocore.protobuf.StmStreamerDrManage.Streamer;

public class Test {
	public static void main(String[] args) throws SQLException {
		/*System.out.println("----1----");
		Object[] para = {"1","2"};
		String sql="select * from test where clientId=? and streamerId=?";
		QueryRunner qr2 = MyDataSource.getQueryRunner();
		//db error
		System.out.println("start too query");
		List<Integer> dbErrors = qr2.query(sql, new ColumnListHandler<Integer>("exception"),para);
		System.out.println(dbErrors.size()+","+dbErrors.toString());*/
		
		//注意这里名称不一致，需要特殊处理
		/*List<String> currentErrors=new ArrayList<String>();
		String excepts="24";
		
		//current error
		if(excepts!=""&&excepts!=null){
			currentErrors.addAll(Arrays.asList(excepts.split(";")));
		}
		
		System.out.println("Current error size:"+currentErrors.size());
		for(String ex:currentErrors){
			System.out.println("Current error:"+ex);
		}*/
		
		/*MySnmp mySnmp=MySnmpCache.getInstance().getMySnmp();
		System.out.println(mySnmp.getStation_name());
		
		MySnmpCache.getInstance().updateMySnmp();
		mySnmp=MySnmpCache.getInstance().getMySnmp();
		System.out.println(mySnmp.getStation_name());*/
		
		StreamerClouddrmanage.GetServerInfoReturn.Builder get= StreamerClouddrmanage.GetServerInfoReturn.newBuilder();
		StreamerClouddrmanage.Streamer.Builder s= StreamerClouddrmanage.Streamer.newBuilder();
		s.setIp("192.168.1.13");
		s.setName("server13");
		s.setTotal(new Long("66006659629056"));
		s.setUsed(new Long("3236257857536"));
		List<StreamerClouddrmanage.FaultType> fault=new ArrayList<StreamerClouddrmanage.FaultType>();
		fault.add(StreamerClouddrmanage.FaultType.NORMAL);
		s.addAllStreamerState(fault);
		//s.setStreamerState(0, FaultType.NORMAL);
		s.setOracleVol(new Long("1087700467712"));
		s.setMaxClients(new Long("128"));

		s.setOracleVol(0);

		s.setMaxClients(128);

		s.setEcsUsed(0L);

		s.setMetaUsed(0L);
		s.setOssUsed(0L);
		s.setRacUsed(0L);
		s.setRdsUsed(0L);
		s.setCloudVol(0L);
		s.setRdsVol(0L);

		StreamerClouddrmanage.Client.Builder c= StreamerClouddrmanage.Client.newBuilder();
		c.setId("6755a773-0000-0000-0000-000000000000");
		c.setName("RACTT");
		c.setIp("");
		c.setType(StreamerClouddrmanage.ClientType.RAC);
		List<StreamerClouddrmanage.FaultType> fault1=new ArrayList<StreamerClouddrmanage.FaultType>();
		fault1.add(StreamerClouddrmanage.FaultType.NORMAL);
		c.addAllClientState(fault1);
		//c.setClientState(0, FaultType.NORMAL);
		c.setSystemVersion("Red Hat Enterprise Linux Server release 6.5 (Santiago)");

		get.addClients(c.build());
		get.setUuid("98e10615-6200-4a92-bd23-40011f5945b7");
		get.setServer(s.build());
		
        try {
            //创建Socket对象
            Socket socket=new Socket("192.168.11.188",1232);
            
            //根据输入输出流和服务端连接
            OutputStream outputStream=socket.getOutputStream();//获取一个输出流，向服务端发送信息
            
            Header header=new Header();
    		header.setCommand(87000);
    		header.setVersion((short) 1);
    		header.setFlags((short) 0);
    		header.setDataType((short) 0);
    		header.setDataLength(0);
    		header.setErrorCode(1);
    		header.setDirection((short) 0);
    		
            byte[] resp=header.toByteArray();
           // outputStream.write(get., 0, get.build().toString());
            
            outputStream.close();
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
