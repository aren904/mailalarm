package cn.infocore.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.infocore.dto.FaultDTO;
import cn.infocore.manager.AlarmLogManager;
import cn.infocore.service.AlarmLogService;
import cn.infocore.service.MailService;

@Service
public class AlarmLogServiceImpl implements AlarmLogService {
    
    @Autowired
    private AlarmLogManager alarmLogManager;
    
    @Autowired
    private MailService mailService;   
    
    /**
	 * 查找指定客户端未确认的异常集合
	 * @param targetUuid
	 * @return
	 */
    @Override
	public List<Integer> findVmUncheckedExceptions(String targetUuid) {
    	return alarmLogManager.findVmUncheckedExceptions(targetUuid);
	}
    
    /**
     * 处理心跳异常：更新/新建，自动确认，发送告警
     */
    @Override
    public void noticeFaults(List<FaultDTO> faults) {
    	for (FaultDTO fault : faults) {
    		alarmLogManager.updateOrAddAlarmlog(fault);
        }
        mailService.sendFaults(faults);
    }

}
