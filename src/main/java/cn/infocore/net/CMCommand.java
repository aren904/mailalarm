package cn.infocore.net;

public enum CMCommand {
	
	ST_OP_ADD_DATA_ARK (1501),
	ST_OP_REMOVE_DATA_ARK (1502),
	ST_OP_UPDATE_DATA_ARK (1505),
	ST_OP_CREATE_EMAIL_ALARM (2301),
	ST_OP_UPDATE_EMAIL_ALARM (2303),
	ST_OP_VERIFY_EMAIL_ALARM (2304),
	ST_OP_UPDATE_SNMP (2401);
	
	private int value;
	
	private CMCommand (int v) {
		this.value = v;
	}
	
	public static CMCommand getCommandCode (int v) {
		for (CMCommand code : CMCommand.values()) {
			if (code.getValue() == v) {
				return code;
			}
		}
		return null;
	}
	
	public int getValue () {
		return this.value;
	}
}
