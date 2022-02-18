package cn.infocore.service;

import java.util.List;

import cn.infocore.dto.FaultDTO;
import cn.infocore.protobuf.StmAlarmManage;

public interface OssService {

    public void updateOssClient(StmAlarmManage.OssInfo ossClient);

    public List<FaultDTO> findFaultFromOssClients(List<StmAlarmManage.OssInfo> ossClients);

}
