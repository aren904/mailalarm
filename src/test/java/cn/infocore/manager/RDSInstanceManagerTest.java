package cn.infocore.manager;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.infocore.entity.RdsInstanceDO;
@SpringBootTest
public class RDSInstanceManagerTest {

	@Autowired
	RDSInstanceManager rDSInstanceManager;
	
	@Test
	void patchInstanceTest(){
		RdsInstanceDO ins = new RdsInstanceDO();
		ins.setId("rm-hp3etguf5iz68d129");
		ins.setName("LLSsssMddddssOIH");
		ins.setExceptions("54sdddsssss;6");
		ins.setType(5);
//		rDSInstanceManager.patchInstance(ins);
	}
}


