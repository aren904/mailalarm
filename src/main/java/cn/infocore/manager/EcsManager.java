package cn.infocore.manager;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.dao.EcsMapper;
import cn.infocore.entity.EcsDO;
@Service
public class EcsManager extends ServiceImpl<EcsMapper, EcsDO>{
    
    public void updateByEcsId(String ecsId, EcsDO ecsDO) {
        LambdaQueryWrapper<EcsDO> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(EcsDO::getEcsId, ecsId);

        this.baseMapper.update(ecsDO, queryWrapper);
    }

}
