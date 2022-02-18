package cn.infocore.manager;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.consts.FaultEnum;
import cn.infocore.dto.FaultDTO;
import cn.infocore.entity.AlarmLog;
import cn.infocore.mapper.AlarmLogMapper;
import cn.infocore.protobuf.StmAlarmManage;
import cn.infocore.utils.ConvertUtils;

@Component
public class AlarmLogManager extends ServiceImpl<AlarmLogMapper, AlarmLog> {
	
    private static final Logger logger = Logger.getLogger(AlarmLogManager.class);
    
    /**
     * 查询指定客户端未确认异常集合
     * @param targetUuid
     * @return
     */
    public List<Integer> findVmUncheckedExceptions(String targetUuid) {
    	LambdaQueryWrapper<AlarmLog> queryWrapper = new LambdaQueryWrapper<>();
    	queryWrapper.eq(AlarmLog::getTargetUuid,targetUuid);
    	queryWrapper.eq(AlarmLog::getProcessed,0);
    	List<AlarmLog> logs=this.list(queryWrapper);
    	List<Integer> exceptions=logs.stream().map(AlarmLog::getException).collect(Collectors.toList());
		return exceptions;
	}
	
    /**
     * 更新日志
     * @param fault
     */
    public void updateOrAddAlarmlog(FaultDTO fault) {
        String targetUuid = fault.getTargetUuid();
        String dataArkUuid = fault.getDataArkUuid();
        String dataArkIp = fault.getDataArkIp();
        String targetName = fault.getTargetName();
        String dataArkName = fault.getDataArkName();
        Collection<StmAlarmManage.FaultType> faultTypes = fault.getFaultTypes();
        Long timestamp = fault.getTimestamp();
        List<String> userIds = fault.getUserUuids();

        //获取该异常所在数据方舟和客户端的所有告警日志并收到集合：已有异常
        List<AlarmLog> currentLogs = findAlarmLogByDataArkUuidAndTargetUuid(dataArkUuid, targetUuid);
        //使用Set保存，不允许重复元素
        Set<Integer> exceptionSet = new HashSet<Integer>();
        for (AlarmLog alarmLogDO : currentLogs) {
            exceptionSet.add(alarmLogDO.getException());
        }
        
        String userIdsString = ConvertUtils.convertListToString(userIds);
        //收集对象的异常集合
        for (StmAlarmManage.FaultType faultType : faultTypes) {
            Integer code = faultType.getNumber();
            AlarmLog alarmLog = new AlarmLog();
            alarmLog.setDataArkName(dataArkName);
            alarmLog.setDataArkUuid(dataArkUuid);
            alarmLog.setDataArkIp(dataArkIp);
            alarmLog.setTargetUuid(targetUuid);
            alarmLog.setTargetName(targetName);
            alarmLog.setUserUuid(userIdsString);
            alarmLog.setException(code);
            alarmLog.setTimestamp(timestamp);
            //更新或插入异常信息
            updateAlarmlog(alarmLog);
            //收集新的异常
            exceptionSet.add(code);
        }
        
        for (Integer integer : exceptionSet) {
        	//如果异常是需要自动确认的，则自动确认
            if (FaultEnum.AUTOCONFIRM.contains(FaultEnum.valueOf(integer))) {
                autoConfirmLog(dataArkUuid, targetUuid, integer);
            }
        }
    }
    
    /**
     * 自动确认指定告警日志
     * @param dataArkId
     * @param targetId
     * @param exception
     */
    public void autoConfirmLog(String dataArkUuid, String targetUuid, Integer exception) {
        AlarmLog alarmLog = new AlarmLog();
        alarmLog.setProcessed(1);
        LambdaUpdateWrapper<AlarmLog> updateWrapper = new UpdateWrapper<AlarmLog>().lambda();
        updateWrapper.eq(AlarmLog::getDataArkUuid, dataArkUuid).eq(AlarmLog::getTargetUuid, targetUuid)
                .eq(AlarmLog::getException, exception);
        this.update(alarmLog, updateWrapper);
    }

    /**
     * 获取指定数据方舟下指定客户端的告警日志集合
     * @param dataArkUuid
     * @param targetUuid
     * @return
     */
    public List<AlarmLog> findAlarmLogByDataArkUuidAndTargetUuid(String dataArkUuid, String targetUuid) {
        LambdaQueryWrapper<AlarmLog> queryWrapper = new LambdaQueryWrapper<AlarmLog>();
        queryWrapper.eq(AlarmLog::getDataArkUuid, dataArkUuid).eq(AlarmLog::getTargetUuid, targetUuid);
        return this.list(queryWrapper);
    }

    /**
     * 更新告警日志：存在异常则需要更新
     * 注意这里如果该异常已存在则更新，不存在则新建
     * @param alarmLog
     */
    @Transactional
    public void updateAlarmlog(AlarmLog alarmLog) {
        if (alarmLog.getException() > 0) {
            LambdaQueryWrapper<AlarmLog> queryWrapper = new LambdaQueryWrapper<AlarmLog>();
            //如果targetUuid和exception类型一致则更新数据库该行数据
            queryWrapper.eq(AlarmLog::getTargetUuid, alarmLog.getTargetUuid()).eq(AlarmLog::getException,alarmLog.getException());
            if (this.count(queryWrapper) > 0) {
                this.update(alarmLog, queryWrapper);
            } else {
                logger.info("Insert new exceptions:"+alarmLog.getTargetUuid()+"|"+alarmLog.getTargetName()+"|"+alarmLog.getException());
                long linuxTimestamp = System.currentTimeMillis() / 1000;
                alarmLog.setTimestamp(linuxTimestamp);
                this.save(alarmLog);
            }
        }
    }

}
