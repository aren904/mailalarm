package cn.infocore.dto;

import java.util.List;

import lombok.Data;
import lombok.ToString;

/**
 * 虚拟机整合
 */
@Data
@ToString
public class VirtualMachineDTO {

	private String uuid;

	private String name;
	
	private String path;
	
	private String exception;
	
	private List<Fault> faults;

	// 对应Vcenter的id字段，外健
	private String vcenter_id;

	// 对应Data_ark的id字段，外健
	private String data_ark_id;
	
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
}
