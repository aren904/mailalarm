package cn.infocore.service;

import java.util.List;

import cn.infocore.bo.FaultSimple;
import cn.infocore.protobuf.StmStreamerDrManage.OssInfo;

public interface OssService {

    List<FaultSimple> updateOssClientList(List<OssInfo> ossClient);

}
