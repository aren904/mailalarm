package cn.infocore.main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import cn.infocore.entity.Data_ark;
import cn.infocore.entity.MySnmp;
import cn.infocore.handler.DataArk2Handler;
import cn.infocore.utils.MyDataSource;

public class SnmpTrapSender {
	private Snmp snmp = null;
	private Address targetAddress = null;
	private TransportMapping<UdpAddress> transport = null;
	
	private static final Logger logger=Logger.getLogger(SnmpTrapSender.class);
	
	protected static String fmt(String format, Object... objs) {
		return String.format("[SnmpTrapSender:] " + format, objs);
	}
	
	public static void run(List<String> uuids){
		logger.info(fmt("Get target streamer from DB,total size:"+uuids.size()));
		
		SnmpTrapSender poc = new SnmpTrapSender();
		MySnmp mySnmp=MySnmpCache.getInstance().getMySnmp();
		try {
			List<Data_ark> data_arks=new ArrayList<Data_ark>();
			for(String uuid:uuids){
				String sql = "select id,name,ip from data_ark where id=?";
				Object[] param = { uuid };
				QueryRunner qr = MyDataSource.getQueryRunner();
				Data_ark data_ark=null;
				try {
					data_ark = qr.query(sql, new DataArk2Handler(), param);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				logger.info(fmt("Target streamer info[Id:%s][IP:%s][Name:%s].",data_ark.getId(),data_ark.getIp(),data_ark.getName()));
				if(data_ark!=null){
					data_arks.add(data_ark);
				}
			}
			
			logger.info(fmt("Start to init target[Name:%s][IP:%s][Port:%s] info.",
					mySnmp.getStation_name(),mySnmp.getStation_ip(),mySnmp.getStation_port()));
			poc.init(mySnmp);
			
			logger.info(fmt("Send trap。。。"));
			poc.sendV2cTrap(mySnmp,data_arks);
			/*ResponseEvent respEvnt = poc.sendV2cTrap(mySnmp,data_arks);
			
			// 解析Response
			if (respEvnt != null && respEvnt.getResponse() != null) {
				Vector<VariableBinding> recVBs = (Vector<VariableBinding>) respEvnt.getResponse().getVariableBindings();
				for (int i = 0; i < recVBs.size(); i++) {
					VariableBinding recVB = recVBs.elementAt(i);
					logger.info(fmt("Response VariableBinding[OID:%s][Variable:%s].",
							recVB.getOid().toString(),recVB.getVariable().toString()));
				}
			}*/
		} catch (Exception e) {
			logger.fatal(fmt("SnmpTrapSender error"), e);
		}
	}
	
	public static void main(String[] args) {
		/*List<String> uuids=new ArrayList<String>();
		uuids.add("1afd73ca-b219-4fe9-9156-4de6d61aff77");
		run(uuids);*/
	}
	
	/**
	 * Snmp V2c 测试发送Trap
	 * @return
	 * @throws IOException 
	 */
	public ResponseEvent sendV2cTrap(MySnmp mySnmp,List<Data_ark> data_arks) throws IOException {
		PDU pdu = new PDU();
		for(int i=0;i<data_arks.size();i++){
			Data_ark data_ark=data_arks.get(i);
			pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.4."+i),new OctetString(data_ark.getName())));  
			pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.3."+i),new OctetString(data_ark.getId())));  
			pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.2."+i),new OctetString(data_ark.getIp())));  
			pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.5."+i),new Integer32(10)));  //离线告警状态是10
		}
		pdu.add(new VariableBinding(new OID("1.3.6.1.6.3.1.1.4.1.0"),new OID("1.3.6.1.4.1.35371.1.3.1"))); //ifcAlarmOfServer 
		pdu.setType(PDU.TRAP);
		
		// 设置管理端对象
		CommunityTarget target = new CommunityTarget(); 
		//target.setCommunity(new OctetString(mySnmp.getRead_community_name())); 
		target.setCommunity(new OctetString(mySnmp.getWrite_community_name()));
		target.setAddress(targetAddress); 
		
		// retry times when commuication error 
		target.setRetries(2); //通信不成功，重试2次
		target.setTimeout(mySnmp.getTimeout_ms()); //超时时间
		if(mySnmp.getVersion()==0){
			target.setVersion(SnmpConstants.version1); 
		}else if(mySnmp.getVersion()==1){
			target.setVersion(SnmpConstants.version2c); //暂时只支持v2c
		}else if(mySnmp.getVersion()==2){
			//目前暂不支持，以后支持需要设置认证
			target.setVersion(SnmpConstants.version3); 
		}
		
		logger.info(fmt("Start to send trap for streamer offline."));
		// send pdu
		return snmp.send(pdu, target);
	}
	
	// 设置管理进程的IP和端口  
	public void init(MySnmp mySnmp) throws IOException{
		//目标主机的ip地址 和 端口号,162接收Trap信息
        targetAddress = GenericAddress.parse("udp:"+mySnmp.getStation_ip()+"/"+mySnmp.getStation_port());
        //使用UDP传输协议
        transport = new DefaultUdpTransportMapping();
        //实例化一个snmp对象
        snmp = new Snmp(transport);
        //程序监听snmp消息
        transport.listen();
	}
}
