package cn.infocore.main;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.infocore.utils.Utils;
import org.apache.log4j.Logger;
import org.newsclub.net.unix.AFUNIXServerSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.springframework.stereotype.Component;


@Component
public class ThreadInformation extends Thread {
    private static final Logger logger = Logger.getLogger(ThreadInformation.class);
//    private static final int C_PORT = 23334;
	private static final String MAIL_ALARM_SOCK="/var/run/mailalarm.sock";
    private ThreadPoolExecutor pool;

    private ThreadInformation() {
        pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        pool.setKeepAliveTime(10, TimeUnit.SECONDS);
        pool.allowCoreThreadTimeOut(true);
    }
    private static class ThreadInformationHolder {
        public static ThreadInformation instance = new ThreadInformation();
    }
    public static ThreadInformation instance() {
        return ThreadInformationHolder.instance;
    }




    @Override
    public void run() {
        AFUNIXServerSocket server=null;
        try {
            final File socketFile = new File(MAIL_ALARM_SOCK);

            if (socketFile.exists() && (! socketFile.isDirectory()) && socketFile.canWrite()) {
                logger.error(Utils.fmt("Socket file [%s] already exists, remove.", MAIL_ALARM_SOCK));
                socketFile.delete();
            }

            logger.info("Java Unix server initialized for CloudManager.");
            server = AFUNIXServerSocket.newInstance();
            server.bind(new AFUNIXSocketAddress(socketFile));

            while (! Thread.interrupted()) {
                logger.info("ServerThread waiting for new incoming connection.");
                Socket incoming = server.accept();
                logger.info("ServerThread new incoming connection.");

                logger.info("Received information...");
                pool.execute(new DealInformation( incoming));
            }

            logger.error("ServerThread stopped.");
        } catch (IOException e) {
            logger.error("Server socket shutdown unexpectedly, halt! e:"+e);
            if(server!=null){
                try {
                    server.close();
                } catch (IOException e1) {

                }
            }
            System.exit(1);
        }

    }
}
