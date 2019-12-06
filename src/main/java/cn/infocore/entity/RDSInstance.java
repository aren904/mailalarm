package cn.infocore.entity;

import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.infocore.protobuf.StmStreamerDrManage.FaultType;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@TableName("rds_instance")
public class RDSInstance {
	
	@TableField(value = "id")
	String id;
	@TableField(value = "type")
	Integer type;
	@TableField(value = "name")
	String name;

	@TableField(value = "db_type")
	String dbType;
	@TableField(value = "exceptions")
	String exceptions;
	@TableField(value = "data_ark_id")
	String dataArkId;
	@TableField(value = "user_id")
	String userId;
	@TableField(value = "rds_id")
	String rdsId;

	
	public RDSInstance setExceptionsWithFaultyTypeList(List<FaultType> faultyList) {
		if (faultyList != null && !faultyList.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < faultyList.size(); i++) {
				Integer exceptionCode = faultyList.get(i).getNumber();
				sb.append(exceptionCode);
				if (i < faultyList.size() - 1) {
					sb.append(";");
				}
			}
			this.exceptions = sb.toString();
		}
		return this;
	}
}
