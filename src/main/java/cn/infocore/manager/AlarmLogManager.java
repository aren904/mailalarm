package cn.infocore.manager;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.bo.FaultSimple;
import cn.infocore.consts.FaultEnum;
import cn.infocore.dao.AlarmLogMapper;
import cn.infocore.entity.AlarmLogDO;
import cn.infocore.main.InfoProcessData;
import cn.infocore.protobuf.StmStreamerDrManage.FaultType;

@Component
public class AlarmLogManager extends ServiceImpl<AlarmLogMapper, AlarmLogDO> {
    private static final Logger logger = Logger.getLogger(AlarmLogManager.class);

    public void updateOrAddStatusBatchByType(List<FaultSimple> faultSimples) {

        for (FaultSimple faultSimple : faultSimples) {
            updateOrAddAlarmlog(faultSimple);
        }
    }

    public void updateOrAddAlarmlog(FaultSimple faultSimple) {

//        logger.info("process log===");
//        logger.info(faultSimple.toString());

        String dataArkId = faultSimple.getDataArkId();
        String dataArkIp = faultSimple.getDataArkIp();
        String targetId = faultSimple.getTargetId();
        String targetName = faultSimple.getTargetName();
        String dataArkName = faultSimple.getDataArkName();
        Collection<FaultType> faultTypes = faultSimple.getFaultTypes();
        Long timestamp = faultSimple.getTimestamp();
        List<String> userIds = faultSimple.getUserIds();

        List<AlarmLogDO> currentLogs = getCurrentAlarmLogByDataArkIdAndTargetId(dataArkId, targetId);

        Set<Integer> exceptionSet = new HashSet<Integer>();

        for (AlarmLogDO alarmLogDO : currentLogs) {
            Integer exc = alarmLogDO.getException();
            exceptionSet.add(exc);
        }

        String userIdsString = getUserIdsString(userIds);

        for (FaultType faultType : faultTypes) {

            Integer code = faultType.getNumber();
            AlarmLogDO alarmLogDO = new AlarmLogDO();
            alarmLogDO.setDataArkName(dataArkName);
            alarmLogDO.setDataArkId(dataArkId);
            alarmLogDO.setDataArkIp(dataArkIp);
            alarmLogDO.setTargetId(targetId);
            alarmLogDO.setTarget(targetName);
            alarmLogDO.setUserId(userIdsString);
            alarmLogDO.setException(code);
            alarmLogDO.setTimestamp(timestamp);
            updateAlarmlog(alarmLogDO);

            exceptionSet.remove(code);
        }

        for (Integer integer : exceptionSet) {

            if (FaultEnum.AUTOCONFIRM.contains(FaultEnum.valueOf(integer))) {
                autoConfirmLog(dataArkId, targetId, integer);
            };
        }

    }

    void autoConfirmLog(String dataArkId, String targetId, Integer exception) {
        AlarmLogDO alarmLogDO = new AlarmLogDO();
        alarmLogDO.setProcessed(1);
        LambdaUpdateWrapper<AlarmLogDO> updateWrapper = new UpdateWrapper<AlarmLogDO>().lambda();
        updateWrapper.eq(AlarmLogDO::getDataArkId, dataArkId).eq(AlarmLogDO::getTargetId, targetId)
                .eq(AlarmLogDO::getException, exception);
        this.update(alarmLogDO, updateWrapper);

    }

    List<AlarmLogDO> getCurrentAlarmLogByDataArkIdAndTargetId(String dataArkId, String targetId) {

        LambdaQueryWrapper<AlarmLogDO> lambdaQueryWrapper = new LambdaQueryWrapper<AlarmLogDO>();
        lambdaQueryWrapper.eq(AlarmLogDO::getDataArkId, dataArkId).eq(AlarmLogDO::getTargetId, targetId);
        return this.list(lambdaQueryWrapper);

    }

    @Transactional
    private void updateAlarmlog(AlarmLogDO alarmLogDO) {

        if (alarmLogDO.getException() > 0) {
            LambdaQueryWrapper<AlarmLogDO> lambdaQueryWrapper = new LambdaQueryWrapper<AlarmLogDO>();
            Integer exception = alarmLogDO.getException();
            lambdaQueryWrapper.eq(AlarmLogDO::getTargetId, alarmLogDO.getTargetId()).eq(AlarmLogDO::getException,
                    exception);

            if (this.count(lambdaQueryWrapper) > 0) {

                this.update(alarmLogDO, lambdaQueryWrapper);
            } else {
                long linuxTimestamp = System.currentTimeMillis() / 1000;
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
