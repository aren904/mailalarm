package cn.infocore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
	
	@TableField("user_id")
	private Long userId;
	
	@TableField("data_ark_id")
	private Long dataArkId;
	
	@TableField("client_count")
	private Integer clientCount;
	
	@TableField("vcenter_vm_count")
	private Integer vcenterVmCount;
	
	@TableField("block")
	private Long block;
	
	@TableField("oracle")
	private Long oracle;
	
	@TableField("ecs")
	private Long ecs;
	
	@TableField("oss")
	private Long oss;
	
	@TableField("rds")
	private Long rds;
	
	@TableField("mdb")
	private Long mdb;
	
}
