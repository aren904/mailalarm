package cn.infocore.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.infocore.entity.MySnmp;
import cn.infocore.manager.MySnmpManager;
import cn.infocore.service.MySnmpService;

@Service
public class MySnmpServiceImpl implements MySnmpService {

    @Autowired	
    private MySnmpManager mySnmpManager;

	@Override
	public MySnmp get() {
		List<MySnmp> mySnmps=mySnmpManager.list();
		return mySnmps.size()>0?mySnmps.get(0):null;
	}
    
    
}
