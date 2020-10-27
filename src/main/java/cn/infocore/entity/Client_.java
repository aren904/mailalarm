package cn.infocore.entity;

import java.util.List;

public class Client_ {
	//客户端id
	private String id;
	//名称：计算机名称
	private String name;
	//别名
	private String alias;
	//客户端ip,可能有多个，以 ; 分隔
	private String ips;
	//客户端所有的异常,以 ; 分隔
	private String except;
	
	private String system_Version;
	
	private List<Fault> fList;
	
	//客户端类型 SINGLE = 0;VMWARE = 1;MSCS = 2;RAC = 3;VC = 4; AIX=5;
	private int type;
	//对应Data_ark中的id字段，是外健
	private String data_ark_id;
	
	//对应User中的id字段，是外健
	private String user_id;
	
	private Integer host_type;
	
	public Integer getHost_type() {
		return host_type;
	}

	public void setHost_type(Integer host_type) {
		this.host_type = host_type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Fault> getfList() {
		return fList;
	}

	public void setFaultList(List<Fault> fList) {
		this.fList = fList;
		StringBuilder string=new StringBuilder();
		for (Fault fault:fList) {
			string.append(Integer.toString(fault.getType()));
			string.append(";");
		}
		string.deleteCharAt(string.length()-1);
		setExcept(string.toString());
	}

	public String getName() {
		return name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getIps() {
		return ips;
	}

	public void setIps(String ips) {
		this.ips = ips;
	}

	public String getExcept() {
		return except;
	}

	public void setExcept(String except) {
		this.except = except;
	}

	public String getData_ark_id() {
		return data_ark_id;
	}

	public void setData_ark_id(String data_ark_id) {
		this.data_ark_id = data_ark_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getSystem_Version() {
		return system_Version;
	}

	public void setSystem_Version(String system_Version) {
		this.system_Version = system_Version;
	}
}
