package cn.infocore.manager;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.dao.EcsInstanceMapper;
import cn.infocore.dao.MdbBackupMapper;
import cn.infocore.entity.EcsInstanceDO;
import cn.infocore.entity.MdbBackupDO;

@Service
public class MetaBackupManager extends ServiceImpl<MdbBackupMapper, MdbBackupDO> {

    public void updateByRealId(String id, MdbBackupDO mdbBackupDO) {
        LambdaQueryWrapper<MdbBackupDO> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(MdbBackupDO::getMdbId, id);

        this.baseMapper.update(mdbBackupDO, queryWrapper);

    }

}
