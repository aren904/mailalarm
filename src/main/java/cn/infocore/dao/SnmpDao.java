package cn.infocore.dao;

import cn.infocore.entity.MySnmp;
import org.springframework.stereotype.Repository;

/**
 * @ProjectName: mailalarm
 * @Package: cn.infocore.dao
 * @ClassName: SnmpDao
 * @Author: zxcdr
 * @Description:
 * @Date: 2021/3/30 15:36
 * @Version: 1.0
 */
@Repository
public interface SnmpDao {
    public MySnmp SelectSnmpByIdIp(String ip);
}
