package cn.infocore.dto;

import lombok.Data;
import lombok.ToString;

/**
 * 对应StreamerClouddrmanage.FaultType，属于FaultDTO的子集
 */
@ToString
@Data
public class Fault {
	
	//时间戳
	private long timestamp;
	
	//异常类型，对应FaultEnum
	private int type;
	
	//数据方舟id
	private String data_ark_uuid;
	
	//数据方舟名字
	private String data_ark_name;
	
	//数据方舟ip
	private String data_ark_ip;
	
	//数据方舟名或客户端名或vc或虚拟机
	private String target_name;
	
	//所属用户
	private String user_uuid;
	
	private Long user_id;
	
	//0 数据方舟 1.客户端 2.VC 3.虚拟机
	private int Client_type;
	
	//客户端UUID
	private String Client_id;

	//目标UUID 
	private String target_uuid;

}
