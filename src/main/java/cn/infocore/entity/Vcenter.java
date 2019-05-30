package cn.infocore.entity;

import java.util.List;

public class Vcenter {

	//vc的id
	private String id;
	//vc的名字
	private String name;
	//vc的ip
	private String ips;
	//vc的异常
	private String excep;
	
	//对应Data_ark中的id字段，是外健
	private String data_ark_id;
	
	private List<Fault> faults;
	
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
		setExcep(string.toString());
	}
	
	public String getId() {
		return id;
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
	
	public String getIps() {
		return ips;
	}
	
	public void setIps(String ips) {
		this.ips = ips;
	}
	
	public String getExcep() {
		return excep;
	}
	
	public void setExcep(String excep) {
		this.excep = excep;
	}

	public String getData_ark_id() {
		return data_ark_id;
	}

	public void setData_ark_id(String data_ark_id) {
		this.data_ark_id = data_ark_id;
	}
	
}
