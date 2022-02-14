package cn.infocore.manager;

import cn.infocore.dao.DateArkMapper;
import cn.infocore.entity.DataArkDO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

@Component
public class DataArkManager extends ServiceImpl< DateArkMapper,DataArkDO> {
}
