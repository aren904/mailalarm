package cn.infocore.SnmpV3Sender;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.Priv3DES;
import org.snmp4j.security.PrivAES128;
import org.snmp4j.security.PrivAES192;
import org.snmp4j.security.PrivAES256;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import cn.infocore.entity.DataArk;
import cn.infocore.entity.MySnmp;

/**
 * @ProjectName: mailalarm
 * @Package: cn.infocore.SnmpV3Sender
 * @ClassName: AP
 * @Author: zxcdr
 * @Description:此类为既认证又有特权
 * @Date: 2021/4/13 19:26
 * @Version: 1.0
 */
public class AP {
    private static final org.apache.log4j.Logger logger = Logger.getLogger(AP.class);

    public static ResponseEvent sendSnmpV3_AP(MySnmp mySnmp, Address targetAddress, List<DataArk> data_arks) throws IOException {
        OctetString userName3 = new OctetString(mySnmp.getSecurityUsername());
        OctetString authPass = new OctetString(mySnmp.getAuthPassword());
        OctetString privPass = new OctetString(mySnmp.getPrivacyPassword());
        logger.debug(userName3);
        logger.debug(authPass);
        logger.debug(privPass);
        TransportMapping transport;
        transport = new DefaultUdpTransportMapping();
        Snmp snmp = new Snmp(transport);
        USM usm = new USM(SecurityProtocols.getInstance(),
                new OctetString(MPv3.createLocalEngineID()), 500);
        SecurityModels.getInstance().addSecurityModel(usm);
        logger.debug(snmp);
        UserTarget target = new UserTarget();

        byte[] enginId = "JL-CC-SNL".getBytes();

        SecurityModels secModels = SecurityModels.getInstance();
        synchronized (secModels) {
            if (snmp.getUSM() == null) {
                secModels.addSecurityModel(usm);
            }

            if(mySnmp.getPrivacyProtocol()==1) {
                snmp.getUSM().addUser(new OctetString(mySnmp.getSecurityUsername()), 
                		new OctetString(enginId), new UsmUser(new OctetString(mySnmp.getSecurityUsername()), 
                				AuthMD5.ID, new OctetString(mySnmp.getAuthPassword()), 
                				Priv3DES.ID, new OctetString(mySnmp.getPrivacyPassword())));
            }
            if(mySnmp.getPrivacyProtocol()==2) {
                snmp.getUSM().addUser(new OctetString(mySnmp.getSecurityUsername()), 
                		new OctetString(enginId), new UsmUser(new OctetString(mySnmp.getSecurityUsername()), 
                				AuthMD5.ID, new OctetString(mySnmp.getAuthPassword()), 
                				PrivAES128.ID, new OctetString(mySnmp.getPrivacyPassword())));
            }
            if(mySnmp.getPrivacyProtocol()==3) {
                snmp.getUSM().addUser(new OctetString(mySnmp.getSecurityUsername()), 
                		new OctetString(enginId), new UsmUser(new OctetString(mySnmp.getSecurityUsername()), 
                				AuthMD5.ID, new OctetString(mySnmp.getAuthPassword()),  
                				PrivAES192.ID, new OctetString(mySnmp.getPrivacyPassword())));
            }
            if(mySnmp.getPrivacyProtocol()==4) {
                snmp.getUSM().addUser(new OctetString(mySnmp.getSecurityUsername()), 
                		new OctetString(enginId), new UsmUser(new OctetString(mySnmp.getSecurityUsername()), 
                				AuthMD5.ID, new OctetString(mySnmp.getAuthPassword()),  
                				PrivAES256.ID, new OctetString(mySnmp.getPrivacyPassword())));
            }
            logger.debug(snmp);

            target.setAddress(targetAddress);
            target.setRetries(2);
            target.setTimeout(mySnmp.getTimeoutMs());
            target.setVersion(SnmpConstants.version3);
            target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
            target.setSecurityName(userName3);

            ScopedPDU pdu = new ScopedPDU();
            pdu.setType(PDU.TRAP);
            VariableBinding v = new VariableBinding();
//            v.setOid(SnmpConstants.sysName);
//            v.setVariable(new OctetString("Snmp Trap V3 Test sendV3 AUTH_PRIV----------"));
//            System.out.println("Snmp Trap V3 Test sendV3 AUTH_PRIV");
//            pdu.add(v);
            for (int i = 0; i < data_arks.size(); i++) {
            	DataArk data_ark = data_arks.get(i);
                pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.4." + i), new OctetString(data_ark.getName())));
//                pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.3." + i), new OctetString(data_ark.getId())));//正常
                pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.3." + i), new OctetString(data_ark.getUuid())));
                pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.2." + i), new OctetString(data_ark.getIp())));
                pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.5." + i), new Integer32(10)));  //离线告警状态是10
            }
            pdu.add(new VariableBinding(new OID("1.3.6.1.6.3.1.1.4.1.0"), new OID("1.3.6.1.4.1.35371.1.3.1"))); //ifcAlarmOfServer
            pdu.setType(PDU.TRAP);
            snmp.setLocalEngine(enginId, 500, 1);
            ResponseEvent send = snmp.send(pdu, target);
            return send;
        }
    }
}
