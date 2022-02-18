package cn.infocore.manager;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.dto.EmailAlarmDTO;
import cn.infocore.entity.EmailAlarm;
import cn.infocore.mapper.MailMapper;

@Service
public class MailManager extends ServiceImpl<MailMapper, EmailAlarm> {

	public List<EmailAlarmDTO> findAllWithUser() {
		// TODO Auto-generated method stub
		return null;
	}

	
    
}
