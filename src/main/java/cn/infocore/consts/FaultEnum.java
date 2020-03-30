package cn.infocore.consts;

import java.util.HashMap;

public enum FaultEnum {
    
    UNKNOW(-1,"未知异常"),
    NORMAL(0,"用于调试，正常指令"),
    CLIENT_LOCAL_LOST(1,"本地丢失"),
    CLIENT_DST_LOST(2,"目标丢失"),
    VMWARE_CREATE_SNAP_FAILED(3,"vmware任务计划创建快照点失败"),
    VMWARE_CBT_DROP(4,"CBT异常"),
    CLIENT_OFFLINE(5,"普通客户端离线"),
    VCENTER_OFFLINE(6,"VCENTER离线"),
    CLUSTER_NODE_EXIST_OFFLINE(7,"集群节点或实例离线"),
    CLUSTER_NODE_ALL_OFFLINE(8,"集群离线"),
    STREAMER_POOL_DISABLE( 9, "存储池异常"),
    STREAMER_OFFLINE (10, "Streamer离线"),
    CLIENT_AUTO_EXPAND_FAILED(11, "自动扩容失败"),
    CLIENT_SNAP_MERGE_FAILED (12, "快照合并失败"),
    ORACLE_STORAGE_DROP (13, "oracle备份空间异常"),
    DR_COPY_FAILED (14, "容灾复制失败"),
    RAC_NODE_EXIST_OFFLINE (15, "RAC节点离线 "),
    RAC_NODE_ALL_OFFLINE(16, "RAC节点全部离线 "),
    RAC_INSTANCE_EXIST_OFFLINE (17, "RAC实例离线 "),
    RAC_INSTANCE_ALL_OFFLINE(18, "RAC实例全部离线"),
    VMWARE_OFFLINE(19, "虚拟机离线"),
    CLIENT_CREATE_SNAP_FAILED(20, "普通客户端创建快照点失败"),
    RAC_CREATE_SNAP_FAILED(21, "Rac客户端创建快照点失败 "),
    DR_SERVER_OFFLINE(22, "容灾服务离线 "),
    STREAMER_POOL_ALMOST_FULL(23, "存储池已超过阈值"),
    STREAMER_CLIENT_DISK_OFFLINE(24, "映射的盘离线(用于AIX客户端)"),
    VMWARE_INIT_MIRROR_FAILED(25, "vmware同步数据失败"),
    VMWARE_SNAPSHOT_POINT_SIZE_IS_ZERO(26, "在线虚拟机创建快照点数据异常"),
    FILE_SINGLE_CREATE_TARGET_FAILED(27, "文件级备份客户端任务计划创建快照点失败"),
    FILE_SINGLE_OFFLINE(28, "文件级备份客户端离线"),
    FILE_SINGLE_LOCAL_LOST(29, "文件级备份客户端本地丢失"),
    FILE_SINGLE_TARGET_LOST(30, "文件级备份客户端目标丢失"),
    RDS_INSTANCE_BACKUP_POINT_DOWNLOAD_FAILED(31, "rds实例备份点下载失败"),
    RDS_INSTANCE_OFFLINE(32, "rds实例离线"),
    RDS_STORAGE_DROP(33, "rds备份空间异常 "),
    RDS_AKSK_CHANGED(34, "rds ak/sk变更"),
    RDS_CLIENT_OFFLINE(35, "rds客户端离线"),
    RDS_BACKUP_SERVICE_OFFLINE(36, "rds备份模块服务异常"),
    CLOUD_STORAGE_DROP(37, "云备份空间异常 "),
    OSS_BACKUP_POINT_DOWNLOAD_FAILED(38, "Oss备份点下载失败"),
    OSS_AKSK_CHANGED(39, "oss ak/sk变更"),
    OSS_CLIENT_OFFLINE(40, "oss客户端离线"),
    OSS_BACKUP_SERVICE_OFFLINE(41, "oss备份模块服务异常"),
    OSS_BACKUP_SPACE_HAS_REACHED_THRESHOLD(42, "oss备份空间达到阈值");
    
    int code;
    String message;
    
    public static HashMap<Integer, FaultEnum> map;
    
    FaultEnum(int code,String message){
        this.code = code;
        this.message = message;
    }
    
    public FaultEnum valueOf(int code){
        
        FaultEnum[] faultEnums = FaultEnum.values();
        
        for (FaultEnum faultEnum : faultEnums) {
            if (faultEnum.code ==code) {
                return faultEnum;
            }
        }
        return FaultEnum.UNKNOW;
    }
    
}
