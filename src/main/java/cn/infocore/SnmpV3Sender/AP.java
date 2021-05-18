package cn.infocore.SnmpV3Sender;

import cn.infocore.dto.DataArkDTO;
import cn.infocore.entity.MySnmp;
import org.apache.log4j.Logger;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.util.List;

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

    public static ResponseEvent sendSnmpV3_AP(MySnmp mySnmp, Address targetAddress, List<DataArkDTO> data_arks) throws IOException {
        OctetString userName3 = new OctetString(mySnmp.getSecurity_username());
        OctetString authPass = new OctetString(mySnmp.getAuthentication_password());
        OctetString privPass = new OctetString(mySnmp.getPrivacy_password());
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

            // add user to the USM
//            snmp.getUSM().addUser(userName3, new OctetString(enginId),
////                    new UsmUser(userName3, AuthMD5.ID, authPass, Priv3DES.ID, privPass)
////            );
            if(mySnmp.getPrivacy_protocol()==1) {
                snmp.getUSM().addUser(new OctetString(mySnmp.getSecurity_username()), new OctetString(enginId), new UsmUser(new OctetString(mySnmp.getSecurity_username()), AuthMD5.ID, new OctetString(mySnmp.getAuthentication_password()), Priv3DES.ID, new OctetString(mySnmp.getPrivacy_password())));
            }
            if(mySnmp.getPrivacy_protocol()==2) {
                snmp.getUSM().addUser(new OctetString(mySnmp.getSecurity_username()), new OctetString(enginId), new UsmUser(new OctetString(mySnmp.getSecurity_username()), AuthMD5.ID, new OctetString(mySnmp.getAuthentication_password()), PrivAES128.ID, new OctetString(mySnmp.getPrivacy_password())));
            }
            if(mySnmp.getPrivacy_protocol()==3) {
                snmp.getUSM().addUser(new OctetString(mySnmp.getSecurity_username()), new OctetString(enginId), new UsmUser(new OctetString(mySnmp.getSecurity_username()), AuthMD5.ID, new OctetString(mySnmp.getAuthentication_password()),  PrivAES192.ID, new OctetString(mySnmp.getPrivacy_password())));
            }
            if(mySnmp.getPrivacy_protocol()==4) {
                snmp.getUSM().addUser(new OctetString(mySnmp.getSecurity_username()), new OctetString(enginId), new UsmUser(new OctetString(mySnmp.getSecurity_username()), AuthMD5.ID, new OctetString(mySnmp.getAuthentication_password()),  PrivAES256.ID, new OctetString(mySnmp.getPrivacy_password())));
            }
            logger.debug(snmp);

            target.setAddress(targetAddress);
            target.setRetries(2);
            target.setTimeout(mySnmp.getTimeout_ms());
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
                DataArkDTO data_ark = data_arks.get(i);
                pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.4." + i), new OctetString(data_ark.getName())));
                pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.3." + i), new OctetString(data_ark.getId())));//正常
//                pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.3." + i), new OctetString(data_ark.getUuid())));
                pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.2." + i), new OctetString(data_ark.getIp())));
                pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.5." + i), new Integer32(10)));  //离线告警状态是10
            }
            pdu.add(new VariableBinding(new OID("1.3.6.1.6.3.1.1.4.1.0"), new OID("1.3.6.1.4.1.35371.1.3.1"))); //ifcAlarmOfServer
            pdu.setType(PDU.TRAP);
            snmp.setLocalEngine(enginId, 500, 1);
            ResponseEvent send = snmp.send(pdu, target);
            //System.out.println(send.getError());

            return send;
        }
    }
}
