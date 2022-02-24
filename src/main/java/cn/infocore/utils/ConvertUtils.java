package cn.infocore.utils;

import java.util.List;

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
    
}
