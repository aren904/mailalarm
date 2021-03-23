package cn.infocore.manager;

import java.util.*;


import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.bo.FaultSimple;
import cn.infocore.consts.FaultEnum;
import cn.infocore.dao.AlarmLogMapper;
import cn.infocore.entity.AlarmLogDO;
import cn.infocore.protobuf.StmStreamerDrManage.FaultType;

@Component
public class AlarmLogManager extends ServiceImpl<AlarmLogMapper, AlarmLogDO> {
    private static final Logger logger = Logger.getLogger(AlarmLogManager.class);
//    @Autowired
//    AlarmLogMapper alarmLogMapper;

    public void updateOrAddStatusBatchByType(List<FaultSimple> faultSimples) {

        for (FaultSimple faultSimple : faultSimples) {
            updateOrAddAlarmlog(faultSimple);
        }
    }

    public void updateOrAddAlarmlog(FaultSimple faultSimple) {

//        logger.warn("process log===");
//        logger.warn(faultSimple.toString());
        String targetId = faultSimple.getTargetId();
        String dataArkId = faultSimple.getDataArkId();
        String dataArkIp = faultSimple.getDataArkIp();
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
    void updateAlarmlog(AlarmLogDO alarmLogDO) {
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

//    public List<Integer> checkVmUncheckedException(String clientId) {
//        LambdaQueryWrapper<AlarmLogDO> alarmLogDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        if (!StringUtils.isEmpty(clientId)) {
//            alarmLogDOLambdaQueryWrapper.eq(AlarmLogDO::getTargetId, clientId).eq(AlarmLogDO::getProcessed, 0);
//        }
//        logger.debug(alarmLogMapper);
//        List<AlarmLogDO> list = alarmLogMapper.selectList(alarmLogDOLambdaQueryWrapper);
//        Integer exception = null;
//        for (AlarmLogDO alarmLogDO : list) {
//            exception = alarmLogDO.getException();
//        }
//        ArrayList<Integer> exceptions = new ArrayList<>();
//        exceptions.add(exception);
////        List<Integer> exceptions=new ArrayList<>();
////        exceptions.add(333);
//        return exceptions;
//    }

}
