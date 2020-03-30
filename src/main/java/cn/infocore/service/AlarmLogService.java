package cn.infocore.service;

import java.util.List;

import cn.infocore.bo.FaultSimple;

public interface AlarmLogService {

    void noticeFaults(List<FaultSimple> faultSimples);
}
