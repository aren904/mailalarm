package cn.infocore.mapper;

import cn.infocore.entity.ClientBackup;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface ClientBackupMapper extends BaseMapper<ClientBackup> {

	public void updateVirtualMachineByUuid(String name, String path, String exception, String version, String uuid);
	
}
