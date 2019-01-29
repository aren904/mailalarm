package cn.infocore.entity;

public class Alarm_log {

	//自增的id
	private long id;
	//是否处理
	private byte processed;
	//该报警最后发送的时间戳
	private long last_alarm_timestamp;
	//异常
	private Fault fault;
	
	
	
	public Fault getFault() {
		return fault;
	}
	public void setFault(Fault fault) {
		this.fault = fault;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public byte getProcessed() {
		return processed;
	}
	public void setProcessed(byte processed) {
		this.processed = processed;
	}
	public long getLast_alarm_timestamp() {
		return last_alarm_timestamp;
	}
	public void setLast_alarm_timestamp(long last_alarm_timestamp) {
		this.last_alarm_timestamp = last_alarm_timestamp;
	}
	
	
	
	
		
}
