package cn.infocore.utils;

public class Utils {
    public static String fmt(String fmt, Object... obj) {
        return String.format(fmt, obj);
    }

    // 获取异常等级
    public static String getAlarmInformationClass(int faultType) {
        String ret = "";
        switch (faultType) {
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
            case 13:
            case 14:
            case 16:
            case 17:
            case 18:
            case 19:
            case 22:
            case 32:
            case 36:

                ret = "故障";
                break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 7:
            case 11:
            case 12:
            case 15:
            case 20:
            case 21:
            case 23:
            case 24:
            case 25:

            case 26:
            case 31:
            case 33:
            case 34:
            case 35:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
                //added by xyr
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:

                ret = "警告";
                break;
            default:
                ret = "正常";
                break;
        }
        return ret;
    }

    // 获取异常类型
    public static String getAlarmInformationType(int faultType) {
        String ret = "";
        switch (faultType) {
            case 0:
                ret = "用于调试，正常指令";
                break;
            case 1:
                ret = "本地丢失";
                break;
            case 2:
                ret = "目标丢失";
                break;
            case 3:
                ret = "VMWare任务计划创建快照点失败";
                break;
            case 4:
                ret = "CBT异常";
                break;
            case 5:
                ret = "普通客户端离线";
                break;
            case 6:
                ret = "VCenter离线";
                break;
            case 7:
                ret = "集群节点存在离线";
                break;
            case 8:
                ret = "集群全部离线";
                break;
            case 9:
                ret = "存储池异常";
                break;
            case 10:
                ret = "数据方舟离线";
                break;
            case 11:
                ret = "自动扩容失败";
                break;
            case 12:
                ret = "快照合并失败";
                break;
            case 13:
                ret = "Oracle备份空间异常";
                break;
            case 14:
                ret = "容灾复制失败";
                break;
            case 15:
                ret = "Oracle客户端存在节点离线";
                break;
            case 16:
                ret = "Oracle客户端所有节点离线";
                break;
            case 17:
                ret = "Oracle客户端存在实例离线";
                break;
            case 18:
                ret = "Oracle客户端所有实例离线";
                break;
            case 19:
                ret = "VMWare虚拟机离线";
                break;
            case 20:
                ret = "普通客户端创建快照点失败";
                break;
            case 21:
                ret = "Oracle客户端创建快照点失败";
                break;
            case 22:
                ret = "容灾端服务异常";
                break;
            case 23:
                ret = "存储池已超过阈值";
                break;
            case 24:
                ret = "映射的盘离线(用于AIX客户端)";
                break;
            case 25:
                ret = "VMWARE同步数据失败";
                break;
            case 26:
                ret = "离线创建快照点";
                break;
            case 31:
                ret = "RDS实例备份点下载失败";
                break;
            case 32:
                ret = "RDS实例离线";
                break;
            case 33:
                ret = "RDS备份空间异常";
                break;
            case 34:
                ret = "RDS备份账户异常";
                break;
            case 35:
                ret = "RDS客户端离线";
                break;
            case 36:
                ret = "RDS备份模块服务异常";
                break;
            case 37:
                ret = "云备份空间异常";
                break;
            case 38:
                ret = "OSS备份点下载失败";
                break;
            case 39:
                ret = "oss 备份账户异常";
                break;
            case 40:
                ret = "oss客户端离线";
                break;
            case 41:
                ret = "oss备份模块服务异常";
                break;
            case 42:
                ret = "oss备份空间达到阈值";
                break;
            case 43:
                ret = "oss备份无法写入";
                break;
            case 44:
                ret = "oss备份源丢失";
                break;
            //add by xyr
            case 45:
                ret = "ECS认证异常";
                break;
            case 46:
                ret = "ECS客户端离线";
                break;
            case 47:
                ret = "ECS实例备份点下载失败";
                break;
            case 48:
                ret = "ECS备份空间达到阈值";
                break;
            case 49:
                ret = "ECS备份目标丢失，本地目录丢失";
                break;
            case 50:
                ret = "ECS实例离线，即源丢失，云上实例离线";
                break;
            case 51:
                ret = "元数据库认证异常，AKSK/IP/Password异常";
                break;
            case 52:
                ret = "元数据库客户端离线";
                break;
            case 53:
                ret = "元数据库备份点下载失败";
                break;
            case 54:
                ret = "元数据库备份空间达到阈值";
                break;
            case 55:
                ret = "元数据库备份目标丢失，本地目录丢失";
                break;
            case 56:
                ret = "元数据库备份源丢失";
                break;
            case 57:
                ret = "RDS目标丢失，本地目录丢失";
                break;

            default:
                ret = "未知异常";
                break;
        }
        return ret;
    }

}
