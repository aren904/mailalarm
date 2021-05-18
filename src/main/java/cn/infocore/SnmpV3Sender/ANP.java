package cn.infocore.SnmpV3Sender;

import cn.infocore.dto.DataArkDTO;
import cn.infocore.entity.MySnmp;
import org.apache.log4j.Logger;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @ProjectName: mailalarm
 * @Package: cn.infocore.SnmpV3Sender
 * @ClassName: ANP
 * @Author: zxcdr
 * @Description:此类为有认证有没有特权
 * @Date: 2021/4/13 19:26
 * @Version: 1.0
 */
@Component
public class ANP {
    private static final Logger logger = Logger.getLogger(ANP.class);

    public static ResponseEvent sendSnmpV3_ANP(MySnmp mySnmp, Address targetAddress, List<DataArkDTO> data_arks) throws IOException {

        OctetString userName2 = new OctetString(mySnmp.getSecurity_username());
        OctetString authPass = new OctetString(mySnmp.getAuthentication_password());
        OctetString privPass = new OctetString(mySnmp.getPrivacy_password());
        logger.debug(userName2);
        logger.debug(authPass);
        logger.debug(privPass);
        TransportMapping transport;
        transport = new DefaultUdpTransportMapping();

        Snmp snmp = new Snmp(transport);

        byte[] enginId = "JL-CC-SNL".getBytes();
        USM usm = new USM(SecurityProtocols.getInstance(),
                new OctetString(
                        enginId
                ), 500);
        SecurityModels.getInstance().addSecurityModel(usm);

        UserTarget target = new UserTarget();


        SecurityModels secModels = SecurityModels.getInstance();
        synchronized (secModels) {
            if (snmp.getUSM() == null) {
                secModels.addSecurityModel(usm);
            }

            // add user to the USM
//            snmp.getUSM().addUser(userName2, new OctetString(enginId),
//                    new UsmUser(userName2, AuthMD5.ID, authPass, Priv3DES.ID, privPass)
//            );
            snmp.getUSM().addUser(new OctetString(mySnmp.getSecurity_username()), new OctetString(enginId), new UsmUser(new OctetString(mySnmp.getSecurity_username()), AuthMD5.ID, new OctetString(mySnmp.getAuthentication_password()), PrivDES.ID, new OctetString(mySnmp.getPrivacy_password())));

            target.setAddress(targetAddress);
            target.setRetries(2);
            target.setTimeout(mySnmp.getTimeout_ms());
            target.setVersion(SnmpConstants.version3);
            target.setSecurityLevel(SecurityLevel.AUTH_NOPRIV);
            target.setSecurityName(userName2);

            ScopedPDU pdu = new ScopedPDU();
            pdu.setType(PDU.NOTIFICATION);
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
            return send;
        }
    }
}
