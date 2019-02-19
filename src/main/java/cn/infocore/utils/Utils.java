package cn.infocore.utils;


public class Utils {

	
	public static String fmt(String fmt,Object...obj) {
		return String.format(fmt, obj);
	}
	
	// 获取异常等级
	public static String getAlarmInformationClass(int faultType) {
		String ret = "";
		switch (faultType) {
		case 1:
		case 2:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
		case 13:
		case 14:
		case 15:
		case 16:
		case 17:
		case 18:
			ret = "故障";
			break;
		case 3:
		case 11:
		case 12:
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
			ret = "本地目标丢失";
			break;
		case 2:
			ret = "目标丢失";
			break;
		case 3:
			ret = "任务计划创建快照点失败";
			break;
		case 4:
			ret = "CBT异常";
			break;
		case 5:
			ret = "客户端离线";
			break;
		case 6:
			ret = "VCenter离线";
			break;
		case 7:
			ret = "集群节点或实例存在离线";
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
			ret = "oracle备份空间异常";
			break;
		case 14:
			ret = "容灾复制失败";
			break;
		case 15:
			ret="RAC节点存在离线";
			break;
		case 16:
			ret="RAC节点全部离线";
			break;
		case 17:
			ret="RAC实例存在离线";
		case 18:
			ret="RAC实例全部离线";
			break;
		default:
			ret = "未知异常";
			break;
		}
		return ret;
	}
}
