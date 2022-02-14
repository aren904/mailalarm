//package cn.infocore.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//
//import java.util.concurrent.ThreadPoolExecutor;
//
//@Configuration
//@EnableAsync
//public class AsyncConfig {
//    @Bean(name = "taskExecutor")
//    public ThreadPoolTaskExecutor asyncExecutor(){
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        //核心线程数
//        executor.setCorePoolSize(10);
//        //最大线程数
//        executor.setMaxPoolSize(50);
//        //队列最大长度
//        executor.setQueueCapacity(1000);
//        //线程吃维护线程所允许的空闲时间
//        executor.setKeepAliveSeconds(100);
//        //线程前缀
//        executor.setThreadNamePrefix("SendOsnStmAsync");
//        //线程对拒绝任务(无线程可用)的处理策略
//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//        executor.initialize();
//        return executor;
//    }
//}
