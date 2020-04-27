package cn.infocore.entity;

public enum MailErrorType {

	//存储池异常
	MAIL_STORAGE_POOL_ERR(1),
	//任务计划创建快照点失败
	MAIL_SNAPSHOT_CREATE_ERR(2),
	//容灾复制失败
	MAIL_DR_COPY_ERR(3),
	//客户端离线
	MAIL_CLIENT_OFFLINE_ERR(4),
	//CBT异常
	MAIL_CBT_ERR(5),
	//ORACLE备份空间异常
	MAIL_ORACLE_SPACE_ERR(6),
	//备份异常，本地丢失
	MAIL_BACKUP_LOCAL_LOST_ERR(7),
	//备份异常，目标丢失
	MAIL_BACKUP_TAR_LOST_ERR(8),
	//自动扩容失败
	MAIL_AUTO_INCRE_ERR(9),
	//快照合并失败
	MAIL_MERGE_SNAP_ERR(10),
	//主机离线
	MAIL_HOST_OFFLINE_ERR(11);
	
    
    
	private int value;
	
	private MailErrorType(int value) {
		this.value=value;
	}
	
	public static MailErrorType getErrorType(int value) {
		for (MailErrorType err:MailErrorType.values()) {
			if (err.getValue()==value) {
				return err;
			}
		}
		return null;
	}
	
	public int getValue() {
		return this.value;
	}
	
}
