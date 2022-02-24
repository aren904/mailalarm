package cn.infocore.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;

import cn.infocore.dto.EmailAlarmDTO;
import cn.infocore.dto.Fault;
import cn.infocore.dto.FaultDTO;
import cn.infocore.entity.EmailAlarm;
import cn.infocore.entity.User;
import cn.infocore.mapper.EmailAlarmMapper;
import cn.infocore.protobuf.StmAlarmManage;
import cn.infocore.service.UserService;

@Service
public class EmailAlarmManager extends ServiceImpl<EmailAlarmMapper, EmailAlarm> {
	
	private static final Logger logger = Logger.getLogger(EmailAlarmManager.class);
	
	@Autowired
    private EmailAlarmMapper mailMapper;
	
	@Autowired
    private UserService userService;
	
	/**
	 * 联合查询，当前用户和邮件的配置
	 * @return
	 */
	public List<EmailAlarmDTO> findAllWithUser() {
		/**
		 * String sql = "select user_id,enabled,exceptions,limit_enabled,limit_suppress_time,sender_email,smtp_address,"
                + "smtp_port,smtp_auth_enabled,smtp_user_uuid,smtp_password,ssl_encrypt_enabled,receiver_emails,role "
                + "from email_alarm,user where email_alarm.user_id=user.id";
		 */
		List<EmailAlarmDTO> emailAlarmDtos = mailMapper.selectJoinList(EmailAlarmDTO.class,
                new MPJLambdaWrapper<EmailAlarm>().selectAll(EmailAlarm.class)
                .select(User::getRole)
                .leftJoin(User.class, User::getId, EmailAlarm::getUserId));
		
		logger.debug("findAllWithUser EmailAlarmDTO size:"+emailAlarmDtos.size());
		return emailAlarmDtos;
	}

	/**
	 * 联合查询，根据用户ID查找邮件配置：没配置就为空
	 * @param userId
	 * @return
	 */
	public EmailAlarmDTO findByUserId(Long userId) {
		/**
		 * String sql = "select user_id,enabled,exceptions,limit_enabled,limit_suppress_time,sender_email,smtp_address,"
                + "smtp_port,smtp_auth_enabled,smtp_user_uuid,smtp_password,ssl_encrypt_enabled,receiver_emails "
                + "from email_alarm,user where email_alarm.user_id=user.id and email_alarm.user_id=?";
		 */
		EmailAlarmDTO emailAlarmDto = mailMapper.selectJoinOne(EmailAlarmDTO.class,
                new MPJLambdaWrapper<EmailAlarm>()
                .selectAll(EmailAlarm.class)
                .select(User::getRole)
                .leftJoin(User.class, User::getId, EmailAlarm::getUserId)
                .eq(EmailAlarm::getUserId, userId));
		return emailAlarmDto;
	}
	
	/**
     * 将FaultDTO转化成Fault集合，拥有该异常的用户都要发
     * @param faultDto
     * @return
     */
    public List<Fault> convertFaultWithUsers(FaultDTO faultDto) {
        List<StmAlarmManage.FaultType> faultTypes = faultDto.getFaultTypes();

        String dataArkId = faultDto.getDataArkUuid();
        String dataArkIp = faultDto.getDataArkIp();
        String data_ark_name = faultDto.getDataArkName();
        String targetId = faultDto.getTargetUuid();
        String targetName = faultDto.getTargetName();
        StmAlarmManage.ClientType clientType = faultDto.getClientType();
        List<String> userUuids = faultDto.getUserUuids();
        Long timestamp = faultDto.getTimestamp();
        
        List<Fault> faults = new ArrayList<Fault>();
        for (StmAlarmManage.FaultType faultType : faultTypes) {
            for (String userUuid : userUuids) {
            	User user=userService.findByUuid(userUuid);
                Integer code = faultType.getNumber();
                Fault fault = new Fault();
                fault.setType(code);
                fault.setClient_id(targetId);
                fault.setClient_type(clientType.getNumber());
                fault.setData_ark_uuid(dataArkId);
                fault.setData_ark_ip(dataArkIp);
                fault.setData_ark_name(data_ark_name);
                fault.setTarget_name(targetName);
                fault.setUser_uuid(userUuid);
                fault.setUser_id(user.getId());
                fault.setTimestamp(timestamp);
                faults.add(fault);
            }
        }
        return faults;
    }
    
}
