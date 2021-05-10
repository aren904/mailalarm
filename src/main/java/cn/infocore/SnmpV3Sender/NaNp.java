package cn.infocore.SnmpV3Sender;

import cn.infocore.dto.DataArkDTO;
import cn.infocore.entity.MySnmp;
import org.apache.log4j.Logger;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.util.List;

/**
 * @ProjectName: mailalarm
 * @Package: cn.infocore.SnmpV3Sender
 * @ClassName: NaNp
 * @Author: zxcdr
 * @Description: 此类为既无认证有没有特权
 * @Date: 2021/4/13 19:25
 * @Version: 1.0
 */
public class NaNp {
    private static final Logger logger = Logger.getLogger(NaNp.class);
    public static ResponseEvent sendSnmpV3_NANP(MySnmp mySnmp, Address targetAddress, List<DataArkDTO> data_arks) throws IOException {
        SNMP4JSettings.setExtensibilityEnabled(true);
        SecurityProtocols.getInstance().addDefaultProtocols();
        TransportMapping transport;
        transport = new DefaultUdpTransportMapping();
        UserTarget target = new UserTarget();
        target.setVersion(SnmpConstants.version3);
        target.setSecurityLevel(SecurityLevel.NOAUTH_NOPRIV);
        target.setAddress(targetAddress);
        Snmp snmp = new Snmp(transport);

        byte[] enginId = "JL-CC-SNL".getBytes();
        USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(
                enginId), 500);
        SecurityModels secModels = SecurityModels.getInstance();
        if (snmp.getUSM() == null) {
            secModels.addSecurityModel(usm);
        }


        ScopedPDU pdu = new ScopedPDU();
        pdu.setType(PDU.NOTIFICATION);

        for (int i = 0; i < data_arks.size(); i++) {
            DataArkDTO data_ark = data_arks.get(i);
            pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.4." + i), new OctetString(data_ark.getName())));
            pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.3." + i), new OctetString(data_ark.getId())));
            pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.2." + i), new OctetString(data_ark.getIp())));
            pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.35371.1.2.1.1.5." + i), new Integer32(10)));  //离线告警状态是10
        }
        pdu.add(new VariableBinding(new OID("1.3.6.1.6.3.1.1.4.1.0"), new OID("1.3.6.1.4.1.35371.1.3.1"))); //ifcAlarmOfServer
        pdu.setType(PDU.TRAP);
        snmp.setLocalEngine(enginId, 500, 1);
        return snmp.send(pdu, target);

    }

}
