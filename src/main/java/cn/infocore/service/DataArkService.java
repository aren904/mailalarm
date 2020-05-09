package cn.infocore.service;

import cn.infocore.dto.DataArkDTO;

public interface DataArkService  {

    void update(DataArkDTO data_ark);
    String getDataArkNameById(String id);
    
}
