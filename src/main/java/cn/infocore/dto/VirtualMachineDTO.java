package cn.infocore.dto;

import java.util.List;

import lombok.Data;

/**
 * 虚拟机整合
 */
@Data
public class VirtualMachineDTO {

	private String id;
	
	private String uuid;

	private String name;
	
	private String path;
	
	private String exception;
	
	private List<Fault> faults;

	// 对应Vcenter的id字段，外健
	private String vcenter_id;

	// 对应Data_ark的id字段，外健
	private String data_ark_id;
	
	// 对应User的id字段，外健
	private String user_id;

	private int system_Version;

	public void setFaults(List<Fault> faults) {
		this.faults = faults;
		StringBuilder string = new StringBuilder();
		for (Fault fault : faults) {
			string.append(Integer.toString(fault.getType()));
			string.append(";");
		}
		if (string.length() > 0) {
			string.deleteCharAt(string.length() - 1);
		}
		setException(string.toString());
	}

	@Override
	public String toString() {
		return "VirtualMachine{" +
				"id='" + id + '\'' +
				", uuid='" + uuid + '\'' +
				", name='" + name + '\'' +
				", path='" + path + '\'' +
				", except='" + exception + '\'' +
				", faults=" + faults +
				", vcenter_id='" + vcenter_id + '\'' +
				", data_ark_id='" + data_ark_id + '\'' +
				", user_id='" + user_id + '\'' +
				", system_Version=" + system_Version +
				'}';
	}
}
