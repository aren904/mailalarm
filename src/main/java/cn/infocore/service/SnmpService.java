package cn.infocore.service;

        import cn.infocore.entity.MySnmp;
        import org.snmp4j.Snmp;

/**
 * @ProjectName: mailalarm
 * @Package: cn.infocore.service
 * @ClassName: SnmpService
 * @Author: zxcdr
 * @Description:
 * @Date: 2021/3/30 15:18
 * @Version: 1.0
 */
public interface SnmpService {
   public MySnmp SelectSnmpByIdIp(String ip);
}
