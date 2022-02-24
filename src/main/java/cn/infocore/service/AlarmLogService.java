package cn.infocore.service;

import java.util.List;

import cn.infocore.dto.FaultDTO;

public interface AlarmLogService {
	
	/**
	 * 处理心跳异常：更新/新建日志信息，要自动确认的自动确认；还有邮件报警
	 * @param faults
	 */
    public void noticeFaults(List<FaultDTO> faults);
    
}
