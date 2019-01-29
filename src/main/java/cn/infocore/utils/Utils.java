package cn.infocore.utils;

import cn.infocore.protobuf.StmStreamerDrManage.FaultType;

public class Utils {

	// 获取异常等级
	public static String getAlarmInformationClass(FaultType faultType) {
		String ret = "";
		switch (faultType) {
		case CLIENT_LOCAL_LOST:
		case CLIENT_DST_LOST:
		case CLIENT_OFFLINE:
		case ORACLE_STORAGE_DROP:
		case VWARE_CBT_DROP:
		case VCENTER_OFFLINE:
		case DR_COPY_FAILED:
		case STREAMER_OFFLINE:
		case EXIST_OFFLINE:
		case ALL_OFFLINE:
		case STREAMER_POOL_DISABLE:
			ret = "故障";
			break;
		case CLIENT_CREATE_SNAP_AILED:
		case CLIENT_AUTO_EXPAND_FAILED:
		case CLIENT_SNAP_MERGE_FAILED:
			ret = "警告";
			break;
		default:
			ret = "正常";
			break;
		}
		return ret;
	}

	// 获取异常类型
	public static String getAlarmInformationType(FaultType faultType) {
		String ret = "";
		switch (faultType) {
		case NORMAL:
			ret = "用于调试，正常指令";
			break;
		case CLIENT_LOCAL_LOST:
			ret = "本地目标丢失";
			break;
		case CLIENT_DST_LOST:
			ret = "目标丢失";
			break;
		case CLIENT_CREATE_SNAP_AILED:
			ret = "任务计划创建快照点失败";
			break;
		case VWARE_CBT_DROP:
			ret = "CBT异常";
			break;
		case CLIENT_OFFLINE:
			ret = "客户端离线";
			break;
		case VCENTER_OFFLINE:
			ret = "VCenter离线";
			break;
		case EXIST_OFFLINE:
			ret = "集群/rac节点或实例离线";
			break;
		case ALL_OFFLINE:
			ret = "集群/rac离线";
			break;
		case STREAMER_POOL_DISABLE:
			ret = "存储池异常";
			break;
		case STREAMER_OFFLINE:
			ret = "数据方舟离线";
			break;
		case CLIENT_AUTO_EXPAND_FAILED:
			ret = "自动扩容失败";
			break;
		case CLIENT_SNAP_MERGE_FAILED:
			ret = "快照合并失败";
			break;
		case ORACLE_STORAGE_DROP:
			ret = "oracle备份空间异常";
			break;
		case DR_COPY_FAILED:
			ret = "容灾复制失败";
			break;

		default:
			ret = "未知异常";
			break;
		}
		return ret;
	}
}
