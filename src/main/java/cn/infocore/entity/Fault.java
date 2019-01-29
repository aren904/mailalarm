package cn.infocore.entity;

import cn.infocore.protobuf.StmStreamerDrManage.FaultType;

public class Fault {
	//时间戳
	private long timestamp;
	//异常类型
	private FaultType type;
	//数据方舟id
	private String data_ark_id;
	//数据方舟名字
	private String data_ark_name;
	//数据方舟ip
	private String data_ark_ip;
	//数据方舟名或客户端名
	private String target;
	
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public FaultType getType() {
		return type;
	}
	public void setType(FaultType type) {
		this.type = type;
	}
	public String getData_ark_id() {
		return data_ark_id;
	}
	public void setData_ark_id(String data_ark_id) {
		this.data_ark_id = data_ark_id;
	}
	public String getData_ark_name() {
		return data_ark_name;
	}
	public void setData_ark_name(String data_ark_name) {
		this.data_ark_name = data_ark_name;
	}
	public String getData_ark_ip() {
		return data_ark_ip;
	}
	public void setData_ark_ip(String data_ark_ip) {
		this.data_ark_ip = data_ark_ip;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}

	
	
}
