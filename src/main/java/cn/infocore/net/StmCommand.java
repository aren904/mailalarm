package cn.infocore.net;

public enum StmCommand {
	
	//接收来自OSNSTM服务的心跳
	ST_OP_MANAGEMENT_HEARTBEAT (87000),
	//向OSNSTM服务发送请求心跳指令
	ST_OP_MAILALARM_GET_HEARTBEAT (87001);
	
	private int value;
	
	private StmCommand (int v) {
		this.value = v;
	}
	
	public static StmCommand getCommandCode (int v) {
		for (StmCommand code : StmCommand.values()) {
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
