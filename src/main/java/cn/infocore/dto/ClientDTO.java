package cn.infocore.dto;

import java.util.List;

import lombok.Data;

/**
 * 有代理客户端整合对象：只包含本服务需要的信息
 */
@Data
public class ClientDTO{
	
	private String uuid;
	
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

}
