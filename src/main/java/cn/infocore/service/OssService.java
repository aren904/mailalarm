package cn.infocore.service;

import java.util.List;

import cn.infocore.bo.FaultSimple;
import cn.infocore.protobuf.StmStreamerDrManage.OssInfo;

public interface OssService {

    //根据  更新客户端
    void ReUpdateOssClient(OssInfo ossClient);
    List<FaultSimple> updateOssClientList(List<OssInfo> ossClient);

}
