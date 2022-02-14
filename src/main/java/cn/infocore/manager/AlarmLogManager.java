package cn.infocore.manager;

import java.util.*;


import StmStreamerDrManage.StreamerClouddrmanage;
import cn.infocore.consts.FaultEnum;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.bo.FaultSimple;
import cn.infocore.dao.AlarmLogMapper;
import cn.infocore.entity.AlarmLogDO;


@Component
public class AlarmLogManager extends ServiceImpl<AlarmLogMapper, AlarmLogDO> {
    private static final Logger logger = Logger.getLogger(AlarmLogManager.class);
    @Autowired
    AlarmLogMapper alarmLogMapper;

    public void updateOrAddStatusBatchByType(List<FaultSimple> faultSimples) {

        for (FaultSimple faultSimple : faultSimples) {
            updateOrAddAlarmlog(faultSimple);
        }
    }

    public void updateOrAddAlarmlog(FaultSimple faultSimple) {

//        logger.warn("process log===");
//        logger.warn(faultSimple.toString());
        String targetId = faultSimple.getTargetUuid();
        String dataArkId = faultSimple.getDataArkUuid();
        String dataArkIp = faultSimple.getDataArkIp();
        String targetName = faultSimple.getTargetName();
        String dataArkName = faultSimple.getDataArkName();
//        Collection<StreamerClouddrmanage.FaultType> faultTypes = faultSimple.getFaultTypes();
        Collection<StreamerClouddrmanage.FaultType> faultTypes = faultSimple.getFaultTypes();
        Long timestamp = faultSimple.getTimestamp();
        List<String> userIds = faultSimple.getUserUuids();

        List<AlarmLogDO> currentLogs = getCurrentAlarmLogByDataArkIdAndTargetId(dataArkId, targetId);
        Set<Integer> exceptionSet = new HashSet<Integer>();
        for (AlarmLogDO alarmLogDO : currentLogs) {
            Integer exc = alarmLogDO.getException();
            exceptionSet.add(exc);
        }
        String userIdsString = getUserIdsString(userIds);
        for (StreamerClouddrmanage.FaultType faultType : faultTypes) {

            Integer code = faultType.getNumber();
            AlarmLogDO alarmLogDO = new AlarmLogDO();
            alarmLogDO.setDataArkName(dataArkName);
            alarmLogDO.setDataArkUuid(dataArkId);
            alarmLogDO.setDataArkIp(dataArkIp);
            alarmLogDO.setTargetUuid(targetId);//
            alarmLogDO.setTargetName(targetName);
            alarmLogDO.setUserUuid(userIdsString);
            alarmLogDO.setException(code);//
            alarmLogDO.setTimestamp(timestamp);
            updateAlarmlog(alarmLogDO);
//            exceptionSet.remove(code);//2.0.0版本
            exceptionSet.add(code);//2.0.1版本
        }
//        logger.debug(exceptionSet);
        for (Integer integer : exceptionSet) {

            if (FaultEnum.AUTOCONFIRM.contains(FaultEnum.valueOf(integer))) {
                autoConfirmLog(dataArkId, targetId, integer);
            }
        }

    }
    //select * from AlarmDO where dataArk=?,targetid=?,exception=?
    void autoConfirmLog(String dataArkId, String targetId, Integer exception) {
        AlarmLogDO alarmLogDO = new AlarmLogDO();
        alarmLogDO.setProcessed(1);
        LambdaUpdateWrapper<AlarmLogDO> updateWrapper = new UpdateWrapper<AlarmLogDO>().lambda();
        updateWrapper.eq(AlarmLogDO::getDataArkUuid, dataArkId).eq(AlarmLogDO::getTargetUuid, targetId)
                .eq(AlarmLogDO::getException, exception);
        this.update(alarmLogDO, updateWrapper);

    }

    List<AlarmLogDO> getCurrentAlarmLogByDataArkIdAndTargetId(String dataArkId, String targetId) {
        LambdaQueryWrapper<AlarmLogDO> lambdaQueryWrapper = new LambdaQueryWrapper<AlarmLogDO>();
        lambdaQueryWrapper.eq(AlarmLogDO::getDataArkUuid, dataArkId).eq(AlarmLogDO::getTargetUuid, targetId);
        return this.list(lambdaQueryWrapper);
    }

    @Transactional
    void updateAlarmlog(AlarmLogDO alarmLogDO) {
        if (alarmLogDO.getException() > 0) {
            LambdaQueryWrapper<AlarmLogDO> lambdaQueryWrapper = new LambdaQueryWrapper<AlarmLogDO>();
            Integer exception = alarmLogDO.getException();
            //如果targetUuid和exception类型一致则更新数据库该行数据
            lambdaQueryWrapper.eq(AlarmLogDO::getTargetUuid, alarmLogDO.getTargetUuid()).eq(AlarmLogDO::getException,
                    exception);
            if (this.count(lambdaQueryWrapper) > 0) {
                this.update(alarmLogDO, lambdaQueryWrapper);
            } else {
                logger.info("insert new exceptions");
                long linuxTimestamp = System.currentTimeMillis() / 1000;
                alarmLogDO.setTimestamp(linuxTimestamp);
                this.save(alarmLogDO);
//                alarmLogMapper.insert(alarmLogDO);
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
