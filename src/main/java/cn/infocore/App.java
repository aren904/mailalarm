package cn.infocore;




import cn.infocore.mail.MailSender;
import org.apache.log4j.Logger;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import cn.infocore.main.MailMain;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@MapperScan("cn.infocore.dao")
//@EnableScheduling
//@EnableAsync
public class App {
//	private static final Logger logger = Logger.getLogger(App.class);
	public static void main(String[] args) throws Exception {

		ConfigurableApplicationContext context = SpringApplication.run(App.class, args);
		MailMain mainRunner = context.getBean(MailMain.class);
		mainRunner.start();
	}
}
