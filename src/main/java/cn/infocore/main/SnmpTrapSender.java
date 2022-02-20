package cn.infocore.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import cn.infocore.SnmpV3Sender.ANP;
import cn.infocore.SnmpV3Sender.AP;
import cn.infocore.SnmpV3Sender.NaNp;
import cn.infocore.entity.DataArk;
import cn.infocore.entity.MySnmp;
import cn.infocore.service.DataArkService;
import cn.infocore.service.MySnmpService;

/**
 * 数据方舟离线触发告警
 */
public class SnmpTrapSender {
	
	private static final Logger logger = Logger.getLogger(SnmpTrapSender.class);

    private Snmp snmp = null;
    
    private static Address targetAddress = null;
    
    private TransportMapping<UdpAddress> transport = null;
    
    private MySnmpService mySnmpService;
    
    private DataArkService dataArkService;
    
    protected static String fmt(String format, Object... objs) {
        return String.format("[SnmpTrapSender:] " + format, objs);
    }
    
    public SnmpTrapSender() {}

	public SnmpTrapSender(DataArkService dataArkService,MySnmpService mySnmpService) {
		this.dataArkService=dataArkService;
		this.mySnmpService=mySnmpService;
	}

	public void run(List<String> uuids) {
        logger.info(fmt("Get target streamer from DB,total size:" + uuids.size()+"|"+uuids.toString()));

        SnmpTrapSender poc = new SnmpTrapSender();
        
        MySnmp mySnmp = MySnmpCache.getInstance(mySnmpService).getMySnmp();
        //不空且启用了
        if (mySnmp != null && mySnmp.getEnabled() == 1) {
            try {
            	//收集离线的数据方舟对象
                List<DataArk> data_arks = new ArrayList<>();
                for (String uuid : uuids) {
                	DataArk dataArk=dataArkService.findByUuid(uuid);
                    logger.info(fmt("Target streamer info[Id:%s][IP:%s][Name:%s].", dataArk.getId(), dataArk.getIp(), dataArk.getName()));
                    if (dataArk != null) {
                        data_arks.add(dataArk);
                    }
                }

                //初始化
                logger.info(fmt("Start to init target[Name:%s][IP:%s][Port:%s][AuthProtocol:%s][PrivProtocol:%s] info.",
                        mySnmp.getStation_name(), mySnmp.getStation_ip(), mySnmp.getStation_port(), mySnmp.getAuth_protocol(), mySnmp.getPrivacy_protocol()));
                poc.init(mySnmp);

                logger.info(fmt("Send trap..."));
                if (mySnmp.getVersion() == 1 || mySnmp.getVersion() == 0) {
                    poc.sendV2cTrap(mySnmp, data_arks);
                    logger.info(fmt("Sending trap is ended"));
                }
                
                //Todo 以下部分8.0版本未实现,下个版本需要将snmp.auth_password和snmp.privacy_password字段解密操作 参考email_alarm表的stmp_password字段 还需完善
                if (mySnmp.getVersion() == 2) {
                    if (mySnmp.getAuth_password_enabled() == 0 && mySnmp.getPrivacy_password_enabled() == 0) {
                        NaNp.sendSnmpV3_NANP(mySnmp,targetAddress,data_arks);
                        logger.debug("your option is NOAUTH_NOPRIV");
                    }
                    if (mySnmp.getAuth_password_enabled() == 1 && mySnmp.getPrivacy_password_enabled() == 1) {
                        AP.sendSnmpV3_AP( mySnmp,targetAddress,data_arks);
                        logger.debug("your option is AUTH_PRIV");
                    }
                    if (mySnmp.getAuth_password_enabled() == 1 && mySnmp.getPrivacy_password_enabled() == 0) {
                        ANP.sendSnmpV3_ANP(mySnmp,targetAddress,data_arks);
                        logger.debug("your option is AUTH_NOPRIV");
                    }
                }
            } catch (Exception e) {
                logger.fatal(fmt("SnmpTrapSender error"), e);
            }
        }
    }

    public ResponseEvent sendV2cTrap(MySnmp mySnmp, List<DataArk> dataArks) throws IOException {
        PDU pdu = new PDU();
        for (int i = 0; i < dataArks.size(); i++) {
            DataArk data_ark = dataArks.get(i);
            pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.4." + i), new OctetString(data_ark.getName())));
            pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.3." + i), new OctetString(data_ark.getId().toString())));//正常
            pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.2." + i), new OctetString(data_ark.getIp())));
            pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.5." + i), new Integer32(10)));  //离线告警状态是10
        }
        pdu.add(new VariableBinding(new OID("1.3.6.1.6.3.1.1.4.1.0"), new OID("1.3.6.1.4.1.35371.1.3.1"))); //ifcAlarmOfServer
        pdu.setType(PDU.TRAP);

        // 设置管理端对象
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(mySnmp.getWrite_comm_name()));
        target.setAddress(targetAddress);

        // retry times when commuication error
        target.setRetries(2); //通信不成功，重试2次
        target.setTimeout(mySnmp.getTimeout_ms()); //超时时间
        if (mySnmp.getVersion() == 0) {
            target.setVersion(SnmpConstants.version1);
        } else if (mySnmp.getVersion() == 1) {
            target.setVersion(SnmpConstants.version2c); //暂时只支持v2c
        }
        logger.info(fmt("Start to send trap for streamer offline."));
        // send pdu
        snmp.send(pdu, target);
        return snmp.send(pdu, target);
    }

    // 设置管理进程的IP和端口
    public void init(MySnmp mySnmp) throws IOException {
        //目标主机的ip地址 和 端口号,162接收Trap信息
        targetAddress = GenericAddress.parse("udp:" + mySnmp.getStation_ip() + "/" + mySnmp.getStation_port());
        //使用UDP传输协议
        transport = new DefaultUdpTransportMapping();
        //实例化一个snmp对象
        snmp = new Snmp(transport);
        //程序监听snmp消息
        transport.listen();
    }
}
