package cn.infocore.entity;

import java.util.List;

public class Virtual_machine {
	private String id;
	private String name;
	private String path;
	private String except;
	private List<Fault> faults;
	//private String type;
	
	//对应Vcenter的id字段，外健
	private String vcenter_id;
	
	//对应Data_ark的id字段，外健
	private String data_ark_id;
	//对应User的id字段，外健
	private String user_id;
	
	private int system_Version;
	
	public List<Fault> getFaults() {
		return faults;
	}
	
	public void setFaults(List<Fault> faults) {
		this.faults = faults;
		StringBuilder string=new StringBuilder();
		for (Fault fault:faults) {
			string.append(Integer.toString(fault.getType()));
			string.append(";");
		}
		string.deleteCharAt(string.length()-1);
		setExcept(string.toString());
	}
	
	public int getSystem_Version() {
		return system_Version;
	}

	public void setSystem_Version(int system_Version) {
		this.system_Version = system_Version;
	}

	public String getId() {
		return id;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getExcept() {
		return except;
	}
	
	public void setExcept(String except) {
		this.except = except;
	}
	
	public String getVcenter_id() {
		return vcenter_id;
	}
	
	public void setVcenter_id(String vcenter_id) {
		this.vcenter_id = vcenter_id;
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
	
}
