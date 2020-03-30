package cn.infocore.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import cn.infocore.bo.FaultSimple;
import cn.infocore.manager.AlarmLogManager;
import cn.infocore.service.AlarmLogService;
import cn.infocore.service.MailService;

@Service
public class AlarmLogServiceImpl implements AlarmLogService {
    
    @Autowired
    AlarmLogManager alarmLogManager;
    
    @Autowired
    MailService mailService;    
    
    @Override
    public void noticeFaults(List<FaultSimple> faultSimples) {
        
        alarmLogManager.updateOrAddStatusBatchByType(faultSimples);
        
        mailService.sentFault(faultSimples);
        
    }

}
