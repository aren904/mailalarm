package cn.infocore.net;

public enum CMRetStatus {
	
	ST_RES_SUCCESS (0),
	ST_RES_FAILED (1),
	ST_RES_ADD_DATA_ARK_FAIL (11501),
	ST_RES_REMOVE_DATA_ARK_FAIL (11502),
	ST_RES_UPDATE_DATA_ARK_FAIL (11505),
	ST_RES_CREATE_EMAIL_ALARM_FAIL (12301),
	ST_RES_UPDATE_EMAIL_ALARM_FAIL (12303),
	ST_RES_VERIFY_EMAIL_ALARM_FAIL (12304),
	ST_RES_UPDATE_SNMP_FAIL (12401);
	
	private int value;
	
	private CMRetStatus (int v) {
		this.value = v;
	}
	
	public static CMRetStatus getRetStatus (int v) {
		for (CMRetStatus status : CMRetStatus.values()) {
			if (status.getValue() == v) {
				return status;
			}
		}
		return null;
	}
	
	public int getValue() {
		return value;
	}
	
}
