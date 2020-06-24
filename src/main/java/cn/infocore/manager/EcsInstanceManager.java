package cn.infocore.manager;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.dao.EcsInstanceMapper;
import cn.infocore.dao.EcsMapper;
import cn.infocore.entity.EcsDO;
import cn.infocore.entity.EcsInstanceDO;
import cn.infocore.entity.OssObjectSetDO;
@Service
public class EcsInstanceManager extends ServiceImpl<EcsInstanceMapper, EcsInstanceDO> {

    public void updateByInstanceId(String id, EcsInstanceDO ecsInstanceDO) {
        LambdaQueryWrapper<EcsInstanceDO> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(EcsInstanceDO::getInstanceId, id);

        this.baseMapper.update(ecsInstanceDO, queryWrapper);
    }

}
