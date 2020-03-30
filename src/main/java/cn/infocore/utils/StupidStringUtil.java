package cn.infocore.utils;

import java.util.Collection;
import java.util.List;

import cn.infocore.protobuf.StmStreamerDrManage.FaultType;

public class StupidStringUtil {

    
    public static String parseExceptionsToFaultyTypeString(List<FaultType> faultyList) {
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
    
    public static String parseUserIdListToUserIdsString(List<String> userIds) {
        if (userIds != null && !userIds.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < userIds.size(); i++) {
                String userId = userIds.get(i);
                sb.append(userId);
                if (i < userIds.size() - 1) {
                    sb.append(";");
                }
            }
            return sb.toString();
        }
        return null;
    }
    
}
