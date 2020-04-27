package cn.infocore.manager;

import java.util.List;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.dao.RDSInstanceMapper;
import cn.infocore.entity.RdsInstanceDO;

@Component
public class RDSInstanceManager extends ServiceImpl<RDSInstanceMapper, RdsInstanceDO> {

    public void updateByUUIDBatch(List<RdsInstanceDO> rdsInstanceList) {

        for (RdsInstanceDO rdsInstance : rdsInstanceList) {
            patchInstance(rdsInstance);
        }
    }

    public boolean patchInstance(RdsInstanceDO instance) {

        String id = instance.getInstanceId();
        boolean isDR = checkDrInstance(id);
        LambdaUpdateWrapper<RdsInstanceDO> updateWrapper = new UpdateWrapper<RdsInstanceDO>().lambda()
                .eq(RdsInstanceDO::getInstanceId, instance.getInstanceId())
                .set(RdsInstanceDO::getName, instance.getName())
                .set(RdsInstanceDO::getExceptions, instance.getExceptions())
                .set(RdsInstanceDO::getSize, instance.getSize())
                .set(RdsInstanceDO::getPreoccupationSize, instance.getPreoccupationSize());
        if (isDR) {
            updateWrapper.set(RdsInstanceDO::getDrSize, instance.getSize())
                .set(RdsInstanceDO::getPreoccupationDrSize, instance.getSize());
        }

        return this.update(new RdsInstanceDO(), updateWrapper);
    }

    boolean checkDrInstance(String instanceId) {
        LambdaQueryWrapper<RdsInstanceDO> queryWrapper = new LambdaQueryWrapper<RdsInstanceDO>()
                .eq(RdsInstanceDO::getInstanceId, instanceId);
        RdsInstanceDO rdsInstanceDO = this.getOne(queryWrapper);
        Integer isDr = rdsInstanceDO.getIsDrEnabled();
        return isDr != null && isDr > 0;

    }

}
