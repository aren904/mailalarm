package cn.infocore.net;

public enum StmRetStatus {
	
	ST_RES_SUCCESS (0),
	ST_RES_FAILED (1);
	
	private int value;
	
	private StmRetStatus (int v) {
		this.value = v;
	}
	
	public static StmRetStatus getRetStatus (short v) {
		for (StmRetStatus status : StmRetStatus.values()) {
			if (status.getShort() == v) {
				return status;
			}
		}
		return null;
	}
	
	public short getShort () {
		return (short) this.value;
	}
}
