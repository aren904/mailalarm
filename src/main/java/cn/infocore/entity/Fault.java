package cn.infocore.entity;

import lombok.ToString;

@ToString
public class Fault {
	//时间戳
	private long timestamp;
	//异常类型
	private int type;
	//数据方舟id
	private String data_ark_id;
	//数据方舟名字
	private String data_ark_name;
	//数据方舟ip
	private String data_ark_ip;
	//数据方舟名或客户端名或vc或虚拟机
	private String target;
	
	//所属用户名
	private String user_id;
	//2019年3月11日18:04:13 朱伟添加
	private Integer Client_type;//0 数据方舟 1.客户端 2.VC 3.虚拟机
	//客户端ID //2019年3月11日18:04:13 朱伟添加
	private String Client_id;
	
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
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
