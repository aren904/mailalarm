package cn.infocore.utils;

import java.util.List;

/**
 * 工具类
 */
public class StupidStringUtil {
    
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
