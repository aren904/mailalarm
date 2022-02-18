package cn.infocore;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import cn.infocore.main.MailMain;

@SpringBootApplication
@MapperScan({"cn.infocore.dao","cn.infocore.mapper"}) //扫描DAO
public class Application {
	
	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
		MailMain mainRunner = context.getBean(MailMain.class);
		mainRunner.start();
	}
	
}
