package cn.infocore.main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import cn.infocore.SnmpV3Sender.ANP;
import cn.infocore.SnmpV3Sender.AP;
import cn.infocore.SnmpV3Sender.NaNp;
import cn.infocore.dao.AlarmLogMapper;
import cn.infocore.entity.AlarmLogDO;
import cn.infocore.manager.AlarmLogManager;
import cn.infocore.utils.BeanUtil;
import javafx.scene.layout.AnchorPane;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import cn.infocore.dto.DataArkDTO;
import cn.infocore.entity.MySnmp;
import cn.infocore.handler.DataArk2Handler;
import cn.infocore.utils.MyDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SnmpTrapSender {
//    @Autowired
//    AlarmLogManager alarmLogManager;
//    public static volatile SnmpTrapSender instance = null;
//
//    public SnmpTrapSender(){
//
//    }
//    public static SnmpTrapSender getInstance(){
//        if(instance == null){
//            synchronized (SnmpTrapSender.class){
//                if(instance == null){
//                    instance = new SnmpTrapSender();
//                }
//            }
//        }
//        return instance;
//    }


//
//    @Autowired
//    AlarmLogMapper alarmLogMapper;

    private Snmp snmp = null;
    private static Address targetAddress = null;
    private TransportMapping<UdpAddress> transport = null;

    private static final Logger logger = Logger.getLogger(SnmpTrapSender.class);

    protected static String fmt(String format, Object... objs) {
        return String.format("[SnmpTrapSender:] " + format, objs);
    }

    public static void run(List<String> uuids) {
//     public  void run(List<String> uuids) {
        logger.info(fmt("Get target streamer from DB,total size:" + uuids.size()));

        SnmpTrapSender poc = new SnmpTrapSender();
        MySnmp mySnmp = MySnmpCache.getInstance().getMySnmp();

        //不空且启用了
        if (mySnmp != null && mySnmp.getEnabled() == 1) {
            try {
                List<DataArkDTO> data_arks = new ArrayList<DataArkDTO>();
                logger.info(uuids);
                for (String uuid : uuids) {
                    String sql = "select name,ip,id from data_ark where uuid=?";
                    Object[] param = {uuid};
                    QueryRunner qr = MyDataSource.getQueryRunner();
                    DataArkDTO data_ark = null;
                    try {
                        data_ark = qr.query(sql, new DataArk2Handler(), param);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    logger.info(data_ark);
                    logger.info(fmt("Target streamer info[Id:%s][IP:%s][Name:%s].", data_ark.getId(), data_ark.getIp(), data_ark.getName()));
                    if (data_ark != null) {
                        data_arks.add(data_ark);
                    }
                }



                logger.info(fmt("Start to init target[Name:%s][IP:%s][Port:%s][AuthProtocol:%s][PrivProtocol:%s] info.",
                        mySnmp.getStation_name(), mySnmp.getStation_ip(), mySnmp.getStation_port(), mySnmp.getAuthentication_protocol(), mySnmp.getPrivacy_protocol()));
                poc.init(mySnmp);

                logger.info(fmt("Send trap。。。"));
                //从alarm_log表中去获取所有异常
//                AlarmLogMapper alarmLogMapper = (AlarmLogMapper)BeanUtil.getBean(AlarmLogMapper.class);
//                logger.info("normal");




                if (mySnmp.getVersion() == 1 || mySnmp.getVersion() == 0) {
//                    poc.sendV2cTrap(mySnmp, data_arks,stringBuilder.toString());
                    poc.sendV2cTrap(mySnmp, data_arks);
                    logger.info(fmt("Sending trap is ended"));
                }

                if (mySnmp.getVersion() == 2) {
                    if (mySnmp.getAuthentication_password_enabled() == 0 && mySnmp.getPrivacy_password_enabled() == 0) {
                        NaNp.sendSnmpV3_NANP(mySnmp,targetAddress,data_arks);
                        logger.debug("your option is NOAUTH_NOPRIV");
                    }
                    if (mySnmp.getAuthentication_password_enabled() == 1 && mySnmp.getPrivacy_password_enabled() == 1) {
                        AP.sendSnmpV3_AP( mySnmp,targetAddress,data_arks);
                        logger.debug("your option is AUTH_PRIV");
                    }
                    if (mySnmp.getAuthentication_password_enabled() == 1 && mySnmp.getPrivacy_password_enabled() == 0) {
                        ANP.sendSnmpV3_ANP(mySnmp,targetAddress,data_arks);
                        logger.debug("your option is AUTH_NOPRIV");
                    }
                    if (mySnmp.getAuthentication_password_enabled() == 1 && mySnmp.getPrivacy_password_enabled() == 0) {
                        logger.error("snmpv3 does not have this option");
                        return;
                    }
                }

            } catch (Exception e) {
                logger.fatal(fmt("SnmpTrapSender error"), e);
            }
        }
    }

    /**
     * Snmp V2c 测试发送Trap
     *
     * @return
     * @throws IOException
     */
//    public ResponseEvent sendV2cTrap(MySnmp mySnmp, List<DataArkDTO> data_arks,String exceptions) throws IOException {
//        PDU pdu = new PDU();
//        for (int i = 0; i < data_arks.size(); i++) {
//            DataArkDTO data_ark = data_arks.get(i);
//            pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.4." + i), new OctetString(data_ark.getName())));
//            pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.3." + i), new OctetString(data_ark.getId())));//正常
////            pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.3." + i), new OctetString(data_ark.getUuid())));
//            pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.2." + i), new OctetString(data_ark.getIp())));
//            pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.5." + i), new Integer32(10)));  //离线告警状态是10
//        }
////        pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.6."), new OctetString(exceptions)));
//        pdu.add(new VariableBinding(new OID("1.3.6.1.6.3.1.1.4.1.0"), new OID("1.3.6.1.4.1.35371.1.3.1"))); //ifcAlarmOfServer
//        pdu.setType(PDU.TRAP);
//
//        // 设置管理端对象
//        CommunityTarget target = new CommunityTarget();
//        //target.setCommunity(new OctetString(mySnmp.getRead_community_name()));
//        target.setCommunity(new OctetString(mySnmp.getWrite_comm_name()));
//        target.setAddress(targetAddress);
//
//        // retry times when commuication error
//        target.setRetries(2); //通信不成功，重试2次
//        target.setTimeout(mySnmp.getTimeout_ms()); //超时时间
//        if (mySnmp.getVersion() == 0) {
//            target.setVersion(SnmpConstants.version1);
//        } else if (mySnmp.getVersion() == 1) {
//            target.setVersion(SnmpConstants.version2c); //暂时只支持v2c
//        }
//        logger.info(fmt("Start to send trap for streamer offline."));
//        // send pdu
//        ResponseEvent send = snmp.send(pdu, target);
//        return snmp.send(pdu, target);
//    }

    public ResponseEvent sendV2cTrap(MySnmp mySnmp, List<DataArkDTO> data_arks) throws IOException {
        PDU pdu = new PDU();
        for (int i = 0; i < data_arks.size(); i++) {
            DataArkDTO data_ark = data_arks.get(i);
            pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.4." + i), new OctetString(data_ark.getName())));
            pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.3." + i), new OctetString(data_ark.getId())));//正常
//            pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.3." + i), new OctetString(data_ark.getUuid())));
            pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.2." + i), new OctetString(data_ark.getIp())));
            pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.5." + i), new Integer32(10)));  //离线告警状态是10
        }
        pdu.add(new VariableBinding(new OID("1.3.6.1.6.3.1.1.4.1.0"), new OID("1.3.6.1.4.1.35371.1.3.1"))); //ifcAlarmOfServer
        pdu.setType(PDU.TRAP);

        // 设置管理端对象
        CommunityTarget target = new CommunityTarget();
        //target.setCommunity(new OctetString(mySnmp.getRead_community_name()));
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
        ResponseEvent send = snmp.send(pdu, target);
        return snmp.send(pdu, target);
    }





//
//    public ResponseEvent sendV3(MySnmp mySnmp, List<DataArkDTO> data_arks) throws IOException {
//        SNMP4JSettings.setExtensibilityEnabled(true);
//        SecurityProtocols.getInstance().addDefaultProtocols();
//        UserTarget target = new UserTarget();
//        target.setVersion(SnmpConstants.version3);
//        try {
//            transport = new DefaultUdpTransportMapping();
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
//
//        target.setAddress(targetAddress);
//
        //不认证、不加密
//        if (mySnmp.getAuthentication_password_enabled() == 0 && mySnmp.getPrivacy_password_enabled() == 0) {
//            target.setSecurityLevel(SecurityLevel.NOAUTH_NOPRIV);
//            logger.debug("your option is NOAUTH_NOPRIV");
//        }
//        //只认证不加密
//        if (mySnmp.getAuthentication_password_enabled() == 1 && mySnmp.getPrivacy_password_enabled() == 0) {
//            target.setSecurityLevel(SecurityLevel.AUTH_NOPRIV);
//            logger.debug("your option is AUTH_NOPRIV");
//        }
//        //既认证、又 加密
//        //设置安全等级
//        if (mySnmp.getAuthentication_password_enabled() == 1 && mySnmp.getPrivacy_password_enabled() == 1) {
//            target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
//            logger.debug("your option is Auth_Priv");
//        }
//        //设置签名方式和加密方式
//        target.setSecurityName(new OctetString(mySnmp.getSecurity_username()));
//        target.setTimeout(mySnmp.getTimeout_ms());
//        target.setRetries(2);
////创建用户安全模型对象
////        USM usm = new USM(SecurityProtocols.getInstance(),
////                new OctetString(MPv3.createLocalEngineID()), 0);
//        byte[] enginId = "JL-CC-SNL".getBytes();
//        USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(
//                enginId), 500);
//        if (snmp.getUSM() == null) {
//            SecurityModels.getInstance().addSecurityModel(usm);
//        }
////添加用户配置信息
//        //create and add the user
//        if (mySnmp.getPrivacy_protocol() == 1) {
//            snmp.getUSM().addUser(new OctetString(mySnmp.getSecurity_username()), new OctetString(enginId), new UsmUser(new OctetString(mySnmp.getSecurity_username()), AuthMD5.ID, new OctetString(mySnmp.getAuthentication_password()), PrivDES.ID, new OctetString(mySnmp.getPrivacy_password())));
//        }
//
//        if (mySnmp.getPrivacy_protocol() == 2) {
//            snmp.getUSM().addUser(new OctetString(mySnmp.getSecurity_username()), new OctetString(enginId), new UsmUser(new OctetString(mySnmp.getSecurity_username()), AuthMD5.ID, new OctetString(mySnmp.getAuthentication_password()), PrivAES128.ID, new OctetString(mySnmp.getPrivacy_password())));
//        }
//
//        if (mySnmp.getPrivacy_protocol() == 3) {
//            snmp.getUSM().addUser(new OctetString(mySnmp.getSecurity_username()), new OctetString(enginId), new UsmUser(new OctetString(mySnmp.getSecurity_username()), AuthMD5.ID, new OctetString(mySnmp.getAuthentication_password()), PrivAES192.ID, new OctetString(mySnmp.getPrivacy_password())));
//        }
//
//        if (mySnmp.getPrivacy_protocol() == 4) {
//            snmp.getUSM().addUser(new OctetString(mySnmp.getSecurity_username()), new OctetString(enginId), new UsmUser(new OctetString(mySnmp.getSecurity_username()), AuthMD5.ID, new OctetString(mySnmp.getAuthentication_password()), PrivAES256.ID, new OctetString(mySnmp.getPrivacy_password())));
//        }
//        ScopedPDU pdu = new ScopedPDU();
////        for (int i = 0; i < data_arks.size(); i++) {
////            DataArkDTO data_ark = data_arks.get(i);
////            pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.4." + i), new OctetString(data_ark.getName())));
////            pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.3." + i), new OctetString(data_ark.getId())));
////            pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.2." + i), new OctetString(data_ark.getIp())));
////            pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.5." + i), new Integer32(10)));  //离线告警状态是10
////        }
////        pdu.add(new VariableBinding(new OID("1.3.6.1.6.3.1.1.4.1.0"), new OID("1.3.6.1.4.1.35371.1.3.1"))); //ifcAlarmOfServer
//        VariableBinding v = new VariableBinding();
//        v.setOid(SnmpConstants.sysName);
//        v.setVariable(new OctetString("Snmp Trap V3 Test sendV3 AUTH_PRIV----------"));
//        pdu.add(v);
//        pdu.setType(PDU.TRAP);
//        logger.debug(pdu.getType());
//        logger.debug(pdu.getVariableBindings());
//        snmp.setLocalEngine(enginId, 500, 1);
//        ResponseEvent send = snmp.send(pdu, target);
//        if (send != null) {
//            logger.error(send);
//            return send;
//        } else {
//            logger.error(send);
//            return null;
//        }
//
//    }


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
