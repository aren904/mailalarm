package cn.infocore.manager;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.bo.FaultSimple;
import cn.infocore.dao.AlarmLogMapper;
import cn.infocore.entity.AlarmLogDO;
import cn.infocore.protobuf.StmStreamerDrManage.FaultType;

@Component
public class AlarmLogManager extends ServiceImpl<AlarmLogMapper, AlarmLogDO> {

    public void updateOrAddStatusBatchByType(List<FaultSimple> faultSimples) {

        for (FaultSimple faultSimple : faultSimples) {
            faultSimple.getClientType();

        }
    }

    public void updateOrAddAlarmlog(FaultSimple faultSimple) {

        String dataArkId = faultSimple.getDataArkId();
        String dataArkIp = faultSimple.getDataArkIp();
        String targetId = faultSimple.getTargetId();
        String targetName = faultSimple.getTargetName();
        Collection<FaultType> faultTypes = faultSimple.getFaultTypes();

        List<String> userIds = faultSimple.getUserIds();

        String userIdsString = getUserIdsString(userIds);

        for (FaultType faultType : faultTypes) {

            Integer code = faultType.getNumber();
            AlarmLogDO alarmLogDO = new AlarmLogDO();

            alarmLogDO.setDataArkId(dataArkId);
            alarmLogDO.setDataArkIp(dataArkIp);
            alarmLogDO.setTargetId(targetId);
            alarmLogDO.setTarget(targetName);
            alarmLogDO.setUserId(userIdsString);
            alarmLogDO.setException(code);
            updateAlarmlog(alarmLogDO);
        }

    }

    @Transactional
    private void updateAlarmlog(AlarmLogDO alarmLogDO) {

        if (alarmLogDO.getException() > 0) {
            LambdaQueryWrapper<AlarmLogDO> lambdaQueryWrapper = new LambdaQueryWrapper<AlarmLogDO>();
            
            lambdaQueryWrapper.eq(AlarmLogDO::getDataArkId, alarmLogDO.getTargetId());
            
            if (this.count(lambdaQueryWrapper)>0) {
                this.update(alarmLogDO, lambdaQueryWrapper);
            }else {
                long linuxTimestamp = System.currentTimeMillis()/1000;
                alarmLogDO.setTimestamp(linuxTimestamp);
                this.save(alarmLogDO);
            }
            
        }

    }

    String getUserIdsString(List<String> userIds) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < userIds.size(); i++) {
            sb.append(userIds.get(i));
            if (i < userIds.size() - 1) {
                sb.append(";");
            }
        }
        return sb.toString();
    }

}
