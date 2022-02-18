package cn.infocore.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 配额
 */
@Accessors(chain = true)
@TableName("quota")
@Data
public class Quota {
	
	@TableId(value = "id")
	private Long id;
	
	private Long user_id;
	
	private Long data_ark_id;
	
	private Integer client_count;
	
	private Integer vcenter_vm_count;
	
	private Long block;
	
	private Long oracle;
	
	private Long ecs;
	
	private Long oss;
	
	private Long rds;
	
	private Long mdb;
	
}
