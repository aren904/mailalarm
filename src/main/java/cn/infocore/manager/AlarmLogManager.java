package cn.infocore.manager;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.dto.Fault;
import cn.infocore.dto.FaultDTO;
import cn.infocore.dto.FaultEnum;
import cn.infocore.entity.AlarmLog;
import cn.infocore.mapper.AlarmLogMapper;
import cn.infocore.protobuf.StmAlarmManage;

@Component
public class AlarmLogManager extends ServiceImpl<AlarmLogMapper, AlarmLog> {
	
    private static final Logger logger = Logger.getLogger(AlarmLogManager.class);
    
    /**
     * 查询指定客户端未确认异常集合（所有用户）
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
        List<String> userUuids = fault.getUserUuids();

        for(String userUuid:userUuids) {
        	 //获取该异常所在数据方舟和客户端的所有告警日志并收到集合：已有异常
            List<AlarmLog> currentLogs = findUnconfirmByDataArkUuidAndTargetUuidAndTargetNameAndUserUuid(dataArkUuid, targetUuid,userUuid);
            
            for (AlarmLog alarmLogDO : currentLogs) {
                //如果异常是需要自动确认的，则自动确认
                if (FaultEnum.AUTOCONFIRM.contains(FaultEnum.valueOf(alarmLogDO.getException()))) {
                    autoConfirmLog(dataArkUuid, targetUuid, userUuid,alarmLogDO.getException());
                }
            }
            
            
            //收集对象的异常集合
            for (StmAlarmManage.FaultType faultType : faultTypes) {
                Integer code = faultType.getNumber();
                AlarmLog alarmLog = new AlarmLog();
                alarmLog.setDataArkName(dataArkName);
                alarmLog.setDataArkUuid(dataArkUuid);
                alarmLog.setDataArkIp(dataArkIp);
                alarmLog.setTargetUuid(targetUuid);
                alarmLog.setTargetName(targetName);
                alarmLog.setUserUuid(userUuid);
                alarmLog.setException(code);
                alarmLog.setTimestamp(timestamp);
                //更新或插入异常信息
                updateAlarmlog(alarmLog);
                
                //如果异常是需要自动确认的，则自动确认
                if (FaultEnum.AUTOCONFIRM.contains(FaultEnum.valueOf(code))) {
                    autoConfirmLog(dataArkUuid, targetUuid, userUuid,code);
                }
            }
        }
    }
    
    /**
     * 自动确认指定用户的指定告警日志
     * @param dataArkUuid
     * @param targetUuid
     * @param userUuid
     * sql = "update alarm_log set processed=1 where data_ark_uuid=? and target_uuid=? and user_uuid=? and exception=?";
     */
    public void autoConfirmLog(String dataArkUuid, String targetUuid, String userUuid,Integer exception) {
        AlarmLog alarmLog = new AlarmLog();
        alarmLog.setProcessed(1);
        LambdaUpdateWrapper<AlarmLog> updateWrapper = new UpdateWrapper<AlarmLog>().lambda();
        updateWrapper.eq(AlarmLog::getDataArkUuid, dataArkUuid).eq(AlarmLog::getTargetUuid, targetUuid)
        	.eq(AlarmLog::getUserUuid, userUuid).eq(AlarmLog::getException, exception);
        this.update(alarmLog, updateWrapper);
    }

    /**
     * 获取指定用户的指定数据方舟下指定客户端的告警日志集合
     * @param dataArkUuid
     * @param targetUuid
     * @param userUuid
     * @return
     */
    public List<AlarmLog> findUnconfirmByDataArkUuidAndTargetUuidAndTargetNameAndUserUuid(String dataArkUuid, String targetUuid,String userUuid) {
        LambdaQueryWrapper<AlarmLog> queryWrapper = new LambdaQueryWrapper<AlarmLog>();
        queryWrapper.eq(AlarmLog::getDataArkUuid, dataArkUuid).eq(AlarmLog::getTargetUuid, targetUuid)
        	.eq(AlarmLog::getUserUuid, userUuid);
        return this.list(queryWrapper);
    }

    /**
     * 更新告警日志：存在异常则需要更新
     * 注意这里如果该异常已存在则更新，不存在则新建
     * @param alarmLog
     */
    @Transactional
    public void updateAlarmlog(AlarmLog alarmLog) {
        try {
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
		} catch (Exception e) {
			logger.error("Failed to updateAlarmlog:"+alarmLog.getException(),e);
		}
    }

    /**
     * 自动确认指定用户的指定数据方舟下指定客户端的异常
     * @param dataArkUuid
     * @param targetUuid
     * @param userUuid
     * sql = "update alarm_log set processed=1 where data_ark_uuid=? and target_uuid=? and user_uuid=? and exception!=3 and exception!=25 and exception!=26";
     */
	public void updateConfirm(String dataArkUuid, String targetUuid,String userUuid) {
		try {
			AlarmLog alarmLog = new AlarmLog();
			alarmLog.setProcessed(1);
			LambdaUpdateWrapper<AlarmLog> updateWrapper = new UpdateWrapper<AlarmLog>().lambda();
			updateWrapper.eq(AlarmLog::getDataArkUuid, dataArkUuid).eq(AlarmLog::getTargetUuid, targetUuid)
				.eq(AlarmLog::getUserUuid, userUuid)
				.ne(AlarmLog::getException, 3).ne(AlarmLog::getException, 25).ne(AlarmLog::getException, 26);
			this.update(alarmLog, updateWrapper);
		} catch (Exception e) {
			logger.error("Failed to updateConfirm:"+dataArkUuid+"|"+targetUuid,e);
		}
	}

	/**
	 * 查询指定用户的指定数据方舟下指定客户端的未确认的异常
	 * @param dataArkUuid
	 * @param targetUuid
	 * @param targetName
	 * sql = "select * from alarm_log where data_ark_uuid=? and binary target_name=? and target_uuid=? and user_uuid=? and processed=0";
	 */
	public List<Integer> findUnconfirmByDataArkUuidAndTargetUuidAndTargetNameAndUserUuid(String dataArkUuid, String targetUuid,
			String targetName,String userUuid) {
		LambdaQueryWrapper<AlarmLog> queryWrapper = new LambdaQueryWrapper<AlarmLog>();
        queryWrapper.eq(AlarmLog::getDataArkUuid, dataArkUuid).eq(AlarmLog::getTargetUuid, targetUuid)
        	.eq(AlarmLog::getTargetName, targetName).eq(AlarmLog::getUserUuid, userUuid).eq(AlarmLog::getProcessed, 0);
        List<AlarmLog> logs= this.list(queryWrapper);
        return logs.stream().map(AlarmLog::getException).collect(Collectors.toList());
	}

	/**
	 * 添加日志
	 * @param fault
	 * sql = "insert into alarm_log(timestamp,processed,exception,data_ark_uuid,data_ark_name,data_ark_ip,
	 * 	target_uuid,target_name,last_alarm_timestamp,user_uuid) values(?,?,?,?,?,?,?,?,?,?)";
	 */
	public void addAlarmlog(Fault fault) {
		AlarmLog alarmLog = new AlarmLog();
		alarmLog.setTimestamp(fault.getTimestamp());
		alarmLog.setProcessed(0);
		alarmLog.setException(fault.getType());
		alarmLog.setDataArkUuid(fault.getData_ark_uuid());
        alarmLog.setDataArkName(fault.getData_ark_name());
        alarmLog.setDataArkIp(fault.getData_ark_ip());
        alarmLog.setTargetUuid(fault.getTarget_uuid());
        alarmLog.setTargetName(fault.getTarget_name());
        alarmLog.setLastAlarmTimestamp(0L);
        alarmLog.setUserUuid(fault.getUser_uuid());
        this.save(alarmLog);
	}

	/**
	 * 更新日志时间
	 * @param fault
	 * @param type
	 * sql = "update alarm_log set timestamp=? where data_ark_uuid=? and target_uuid=? and user_uuid=? and exception=? and processed=0";
	 */
	public void updateAlarmlogTimestamp(Fault fault, Integer type) {
		try {
			AlarmLog alarmLog = new AlarmLog();
			alarmLog.setTimestamp(fault.getTimestamp());;
			LambdaUpdateWrapper<AlarmLog> updateWrapper = new UpdateWrapper<AlarmLog>().lambda();
			updateWrapper.eq(AlarmLog::getDataArkUuid, fault.getData_ark_uuid()).eq(AlarmLog::getTargetUuid, fault.getTarget_uuid())
				.eq(AlarmLog::getUserUuid, fault.getUser_uuid()).eq(AlarmLog::getException, type).eq(AlarmLog::getProcessed, 0);
			this.update(alarmLog, updateWrapper);
		} catch (Exception e) {
			logger.error("Failed to updateAlarmlogTimestamp.",e);
		}
	}

}
