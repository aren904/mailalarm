package cn.infocore.service.impl;

import cn.infocore.dao.SnmpDao;
import cn.infocore.entity.MySnmp;
import cn.infocore.service.SnmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ProjectName: mailalarm
 * @Package: cn.infocore.service.impl
 * @ClassName: SnmpServiceImpl
 * @Author: zxcdr
 * @Description:
 * @Date: 2021/3/30 15:19
 * @Version: 1.0
 */
@Service
public class SnmpServiceImpl implements SnmpService {
@Autowired
    SnmpDao snmpDao;

    @Override
    public MySnmp SelectSnmpByIdIp(String ip) {
        return snmpDao.SelectSnmpByIdIp(ip);
    }
}
