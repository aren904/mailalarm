//package cn.infocore.Test;
//
////import cn.infocore.main.NoticeIpToOsnstm;
//import cn.infocore.main.CaptureDataArkIp;
//import cn.infocore.main.NoticeIpToOsnstm1;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//@SpringBootTest
//public class Tt {
//
////    @Autowired
////    NoticeIpToOsnstm noticeIpToOsnstm;
//    @Autowired
//    CaptureDataArkIp captureDataArkIp;
//
//    @Test
//    public void test() {
////        noticeIpToOsnstm.start();
////    }
//
//        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
////        scheduledExecutorService.scheduleAtFixedRate(new NoticeIpToOsnstm1(),1,60, TimeUnit.SECONDS);
//        scheduledExecutorService.scheduleAtFixedRate(new NoticeIpToOsnstm1(captureDataArkIp), 1, 60, TimeUnit.SECONDS);
//
//    }
//}
//
