package cn.infocore.manager;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;

import cn.infocore.dto.EmailAlarmDTO;
import cn.infocore.entity.EmailAlarm;
import cn.infocore.entity.User;
import cn.infocore.mapper.MailMapper;

@Service
public class MailManager extends ServiceImpl<MailMapper, EmailAlarm> {
	
	private static final Logger logger = Logger.getLogger(MailManager.class);
	
	@Autowired
    private MailMapper mailMapper;
	
	public List<EmailAlarmDTO> findAllWithUser() {
		/**
		 * String sql = "select user_id,enabled,exceptions,limit_enabled,limit_suppress_time,sender_email,smtp_address,"
                + "smtp_port,smtp_auth_enabled,smtp_user_uuid,smtp_password,ssl_encrypt_enabled,receiver_emails,role "
                + "from email_alarm,user where email_alarm.user_id=user.id";
		 */
		List<EmailAlarmDTO> emailAlarmDtos = mailMapper.selectJoinList(EmailAlarmDTO.class,
                new MPJLambdaWrapper<EmailAlarm>()
                .selectAll(EmailAlarm.class)
                .select(User::getRole)
                .leftJoin(User.class, User::getId, EmailAlarm::getUser_id));
		
		logger.debug("findAllWithUser EmailAlarmDTO size:"+emailAlarmDtos.size());
		return emailAlarmDtos;
	}

	public List<EmailAlarmDTO> findWithUserByUserId(Long userId) {
		/**
		 * String sql = "select user_id,enabled,exceptions,limit_enabled,limit_suppress_time,sender_email,smtp_address,"
                + "smtp_port,smtp_auth_enabled,smtp_user_uuid,smtp_password,ssl_encrypt_enabled,receiver_emails "
                + "from email_alarm,user where email_alarm.user_id=user.id and email_alarm.user_id=?";
		 */
		List<EmailAlarmDTO> emailAlarmDtos = mailMapper.selectJoinList(EmailAlarmDTO.class,
                new MPJLambdaWrapper<EmailAlarm>()
                .selectAll(EmailAlarm.class)
                .select(User::getRole)
                .leftJoin(User.class, User::getId, EmailAlarm::getUser_id)
                .eq(EmailAlarm::getUser_id, userId));
		
		logger.debug("findWithUserByUserId EmailAlarmDTO size:"+emailAlarmDtos.size());
		return emailAlarmDtos;
	}
    
}
