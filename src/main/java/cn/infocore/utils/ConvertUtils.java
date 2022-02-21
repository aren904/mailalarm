package cn.infocore.utils;

import java.util.ArrayList;
import java.util.List;

import cn.infocore.dto.Fault;
import cn.infocore.dto.FaultDTO;
import cn.infocore.protobuf.StmAlarmManage;

/**
 * 各种转换工具
 */
public class ConvertUtils {

	/**
	 * 将异常集合集合转String
	 * @param faultyList
	 * @return
	 */
    public static String convertFaultTypesToString(List<StmAlarmManage.FaultType> faultyList) {
        if (faultyList != null && !faultyList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < faultyList.size(); i++) {
                Integer exceptionCode = faultyList.get(i).getNumber();
                sb.append(exceptionCode);
                if (i < faultyList.size() - 1) {
                    sb.append(";");
                }
            }
            return sb.toString();
        }
        return null;
    }
    
    /**
     * 集合转String
     * @param ids
     * @return
     */
    public static String convertListToString(List<String> ids) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            sb.append(ids.get(i));
            if (i < ids.size() - 1) {
                sb.append(";");
            }
        }
        return sb.toString();
    }
    
    /**
     * 将FaultDTO转化成Fault集合，拥有该异常的用户都要发
     * @param faultDto
     * @return
     */
    public static List<Fault> convertFaultWithUsers(FaultDTO faultDto) {
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
                fault.setTimestamp(timestamp);
                faults.add(fault);
            }
        }
        return faults;
    }
    
    /**
     * 管理员：将FaultDTO转化成Fault集合
     * @param faultDto
     * @return
     */
    public static List<Fault> convertFault(FaultDTO faultDto) {
        List<StmAlarmManage.FaultType> faultTypes = faultDto.getFaultTypes();

        String dataArkId = faultDto.getDataArkUuid();
        String dataArkIp = faultDto.getDataArkIp();
        String data_ark_name = faultDto.getDataArkName();
        String targetId = faultDto.getTargetUuid();
        String targetName = faultDto.getTargetName();
        long timestamp = faultDto.getTimestamp();
        StmAlarmManage.ClientType clientType = faultDto.getClientType();
        
        List<Fault> faults = new ArrayList<Fault>();
        for (StmAlarmManage.FaultType faultType : faultTypes) {
            Integer code = faultType.getNumber();
            Fault fault = new Fault();
            fault.setType(code);
            fault.setClient_id(targetId);
            fault.setClient_type(clientType.getNumber());
            fault.setData_ark_uuid(dataArkId);
            fault.setData_ark_ip(dataArkIp);
            fault.setData_ark_name(data_ark_name);
            fault.setTarget_name(targetName);
            fault.setTimestamp(timestamp);
            faults.add(fault);
        }
        return faults;
    }
    
}
