package cn.infocore.consts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public enum FaultEnum {

    UNKNOW(-1, "未知异常"),
    NORMAL(0, "用于调试，正常指令"),
    CLIENT_LOCAL_LOST(1, "本地丢失"),
    CLIENT_DST_LOST(2, "目标丢失"),
    VMWARE_CREATE_SNAP_FAILED(3, "vmware任务计划创建快照点失败"),
    VMWARE_CBT_DROP(4, "CBT异常"),
    CLIENT_OFFLINE(5, "普通客户端离线"),
    VCENTER_OFFLINE(6, "VCENTER离线"),
    CLUSTER_NODE_EXIST_OFFLINE(7, "集群节点或实例离线"),
    CLUSTER_NODE_ALL_OFFLINE(8, "集群离线"),
    STREAMER_POOL_DISABLE(9, "存储池异常"),
    STREAMER_OFFLINE(10, "Streamer离线"),
    CLIENT_AUTO_EXPAND_FAILED(11, "自动扩容失败"),
    CLIENT_SNAP_MERGE_FAILED(12, "快照合并失败"),
    ORACLE_STORAGE_DROP(13, "oracle备份空间异常"),
    DR_COPY_FAILED(14, "容灾复制失败"),
    RAC_NODE_EXIST_OFFLINE(15, "RAC节点离线 "),
    RAC_NODE_ALL_OFFLINE(16, "RAC节点全部离线 "),
    RAC_INSTANCE_EXIST_OFFLINE(17, "RAC实例离线 "),
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
    OSS_BACKUP_SPACE_HAS_REACHED_THRESHOLD(42, "oss备份空间达到阈值"),
    //    added by xyr
    OSS_BACKUP_DST_LOST(43, "OSS备份目标丢失,本地目录丢失"),
    OSS_BACKUP_SOURCE_LOST(44, "OSS备份源丢失，即云上桶或目录丢失"),
    ECS_AUTH_ABNORMAL(45, "ECS认证异常"),
    ECS_CLIENT_OFFLINE(46, "ECS客户端离线"),
    ECS_INSTANCE_BACKUP_POINT_DOWNLOAD_FAILED(47, "ECS实例备份点下载失败"),
    ECS_BACKUP_SPACE_HAS_REACHED_THRESHOLD(48, "ECS备份空间达到阈值"),
    ECS_BACKUP_DST_LOST(49, "ECS备份目标丢失，本地目录丢失"),
    ECS_INSTANCE_OFFLINE(50, "ECS实例离线，即源丢失，云上实例离线"),
    META_AUTH_ABNORMAL(51, "元数据库认证异常，AKSK/IP/Password异常"),
    META_CLIENT_OFFLINE(52, "元数据库客户端离线"),
    META_INSTANCE_BACKUP_POINT_DOWNLOAD_FAILED(53, " 元数据库备份点下载失败"),
    META_BACKUP_SPACE_HAS_REACHED_THRESHOLD(54, "元数据库备份空间达到阈值"),
    META_BACKUP_DST_LOST(55, "元数据库备份目标丢失，本地目录丢失"),
    META_BACKUP_SOURCE_LOST(56, "元数据库备份源丢失"),
    RDS_BACKUP_DST_LOST(57, "RDS目标丢失，本地目录丢失"), 
    ORACLE_BACKUP_SPACE_HAS_EXCEEDED_THRESHOLD(58,"Oracle备份空间达到阈值" ),
    CLIENT_INIT_MIRROR_FAILED(59,"代理客户端同步数据失败"),
    STMVDA_SERVICE_OFFLINE(60,"stmvda 服务离线"),
    STMVDP_SERVICE_OFFLINE(61,"stmvdp 服务离线"),
    STMRECOVERY_SERVICE_OFFLINE(62,"stmrecovery 服务离线"),
    SCMP_SERVICE_OFFLINE(63,"scmp 服务离线"),
    OSNSAN_SERVICE_OFFLINE(64,"osnsan 服务离线"),
    OSNIBRS_SERVICE_OFFLINE(65,"osnibrs 服务离线"),
    META_BACKUP_SERVICE_OFFLINE(66,"metabackup 服务离线"),
    ECS_BACKUP_SERVICE_OFFLINE(67,"ecsbackup 服务离线");

    int code;
    String message;

    public static HashMap<Integer, FaultEnum> map;

    FaultEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static FaultEnum valueOf(int code) {
        FaultEnum[] faultEnums = FaultEnum.values();
        for (FaultEnum faultEnum : faultEnums) {
            if (faultEnum.code == code) {
                return faultEnum;
            }
        }
        return FaultEnum.UNKNOW;
    }

    //不要用ArrayList去存储性能不如HashSet集合
    public static final Set<FaultEnum> AUTOCONFIRM = new HashSet<>();

    static {
        //为了图方便把所有自动确认的异常加进来，因为不确定osnstm会怎么发保险起见
        //需要加上oracle总配额预警 也就是58异常
        AUTOCONFIRM.add(STREAMER_POOL_DISABLE);
        AUTOCONFIRM.add(CLIENT_OFFLINE);
        AUTOCONFIRM.add(VMWARE_CBT_DROP);
        AUTOCONFIRM.add(ORACLE_STORAGE_DROP);
        AUTOCONFIRM.add(CLIENT_LOCAL_LOST);
        AUTOCONFIRM.add(CLIENT_DST_LOST);
        AUTOCONFIRM.add(STREAMER_OFFLINE);
        AUTOCONFIRM.add(VCENTER_OFFLINE);
        AUTOCONFIRM.add(CLUSTER_NODE_ALL_OFFLINE);
        AUTOCONFIRM.add(CLUSTER_NODE_EXIST_OFFLINE);
        AUTOCONFIRM.add(RAC_NODE_EXIST_OFFLINE);
        AUTOCONFIRM.add(RAC_NODE_ALL_OFFLINE);
        AUTOCONFIRM.add(RAC_INSTANCE_ALL_OFFLINE);
        AUTOCONFIRM.add(RAC_INSTANCE_EXIST_OFFLINE);
        AUTOCONFIRM.add(DR_SERVER_OFFLINE);
        AUTOCONFIRM.add(STREAMER_POOL_ALMOST_FULL);
        //这些异常是云平台相关异常只要有问题必定插入
        AUTOCONFIRM.add(RDS_AKSK_CHANGED);
        AUTOCONFIRM.add(RDS_CLIENT_OFFLINE);
        AUTOCONFIRM.add(RDS_INSTANCE_OFFLINE);
        AUTOCONFIRM.add(ECS_BACKUP_DST_LOST);
        AUTOCONFIRM.add(ECS_CLIENT_OFFLINE);
        AUTOCONFIRM.add(ECS_AUTH_ABNORMAL);
        AUTOCONFIRM.add(ECS_BACKUP_SPACE_HAS_REACHED_THRESHOLD);
        AUTOCONFIRM.add(META_CLIENT_OFFLINE);
        AUTOCONFIRM.add(META_BACKUP_SOURCE_LOST);
        AUTOCONFIRM.add(META_AUTH_ABNORMAL);
        AUTOCONFIRM.add(META_BACKUP_DST_LOST);
        AUTOCONFIRM.add(META_BACKUP_SPACE_HAS_REACHED_THRESHOLD);
        AUTOCONFIRM.add(OSS_BACKUP_SOURCE_LOST);
        AUTOCONFIRM.add(OSS_AKSK_CHANGED);
        AUTOCONFIRM.add(OSS_CLIENT_OFFLINE);
        AUTOCONFIRM.add(OSS_BACKUP_SPACE_HAS_REACHED_THRESHOLD);
        AUTOCONFIRM.add(ORACLE_BACKUP_SPACE_HAS_EXCEEDED_THRESHOLD);
        AUTOCONFIRM.add(CLIENT_INIT_MIRROR_FAILED);
        AUTOCONFIRM.add(STMVDA_SERVICE_OFFLINE);
        AUTOCONFIRM.add(STMVDP_SERVICE_OFFLINE);
        AUTOCONFIRM.add(STMRECOVERY_SERVICE_OFFLINE);
        AUTOCONFIRM.add(SCMP_SERVICE_OFFLINE);
        AUTOCONFIRM.add(OSNSAN_SERVICE_OFFLINE);
        AUTOCONFIRM.add(OSNIBRS_SERVICE_OFFLINE);
        AUTOCONFIRM.add(META_BACKUP_SERVICE_OFFLINE);
        AUTOCONFIRM.add(ECS_BACKUP_SERVICE_OFFLINE);
    }
}
