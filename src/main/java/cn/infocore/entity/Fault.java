package cn.infocore.entity;

import lombok.ToString;

@ToString
public class Fault {
	//时间戳
	private long timestamp;
	//异常类型
	private int type;
	//数据方舟id
	private String data_ark_uuid;
	//数据方舟名字
	private String data_ark_name;
	//数据方舟ip
	private String data_ark_ip;
	//数据方舟名或客户端名或vc或虚拟机
	private String target_name;
	
	//所属用户名
	private String user_uuid;
	//2019年3月11日18:04:13 朱伟添加
	private Integer Client_type;//0 数据方舟 1.客户端 2.VC 3.虚拟机
	//客户端ID //2019年3月11日18:04:13 朱伟添加
	private String Client_id;

	private String target_uuid;

	public String getTarget_uuid() {
		return target_uuid;
	}

	public void setTarget_uuid(String target_uuid) {
		this.target_uuid = target_uuid;
	}

	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

	public String getData_ark_uuid() {
		return data_ark_uuid;
	}

	public void setData_ark_uuid(String data_ark_uuid) {
		this.data_ark_uuid = data_ark_uuid;
	}

	public String getTarget_name() {
		return target_name;
	}

	public void setTarget_name(String target_name) {
		this.target_name = target_name;
	}

	public String getUser_uuid() {
		return user_uuid;
	}

	public void setUser_uuid(String user_uuid) {
		this.user_uuid = user_uuid;
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
	public Integer getClient_type() {
		return Client_type;
	}
	public void setClient_type(Integer client_type) {
		Client_type = client_type;
	}
	public String getClient_id() {
		return Client_id;
	}
	public void setClient_id(String client_id) {
		Client_id = client_id;
	}
}
