package cn.infocore.main;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import StmStreamerDrManage.StreamerClouddrmanage;
import lombok.Data;
import org.apache.log4j.Logger;
import cn.infocore.operator.Header;
//import cn.infocore.protobuf.StmStreamerDrManage.GetServerInfoReturn;
import cn.infocore.service.AlarmLogService;
import cn.infocore.service.DataArkService;
import cn.infocore.service.OssService;
import cn.infocore.service.RDSService;
import cn.infocore.service.impl.EcsService;
import cn.infocore.service.impl.MdbService;
import cn.infocore.utils.Utils;

@Data
public class DealSocket implements Runnable {
    private static final Logger logger = Logger.getLogger(DealSocket.class);
    private Socket socket;
    RDSService rdsService;
    EcsService ecsService;
    MdbService mdbService;
    DataArkService dataArkService;
    OssService ossService;
    AlarmLogService alarmLogService;

    public Header getHeaderObj() {
        Header header = new Header();
        header.setCommand(87000);
        header.setVersion((short) 1);
        header.setFlags((short) 0);
        header.setDataType((short) 0);
        header.setDataLength(0);
        header.setErrorCode(1);
        header.setDirection((short) 0);
        return header;
    }

    /**
     * 定义需求，收到指令后需要恢复streamer服务端
     */
    @Override
    public void run() {
        int ioret;
        InputStream in = null;
        OutputStream out = null;
        try {
            in = this.socket.getInputStream();
            out = this.socket.getOutputStream();
            byte[] h = new byte[Header.STREAMER_HEADER_LENGTH];
            ioret = in.read(h, 0, Header.STREAMER_HEADER_LENGTH);
//            logger.debug(ioret);
            if (ioret != Header.STREAMER_HEADER_LENGTH) {
                logger.error(Utils.fmt("Failed to receive header,[%d] byte(s) expected,but [%d] is received.",
                        Header.STREAMER_HEADER_LENGTH, ioret));
                Header header = getHeaderObj();
                byte[] resp = header.toByteArray();
                out.write(resp, 0, resp.length);
                out.flush();
                throw new Exception();
            }

            Header header = new Header();
            header.parseByteArray(h);

            if (header.getCommand() != 87000) {
                logger.error(Utils.fmt("Incorrect command"));
                header.setErrorCode(1);
                byte[] resp = header.toByteArray();
                out.write(resp, 0, resp.length);
                out.flush();
                throw new Exception();
            }

            byte[] buffer = new byte[header.getDataLength()];
            ioret = in.read(buffer, 0, buffer.length);
            if (ioret != buffer.length) {
                logger.error(Utils.fmt("Failed to receive Protobuf"));
            }
            logger.info("Received heartbeat from data_ark.");
            /* GetServerInfoReturn hrt=GetServerInfoReturn.parseFrom(buffer); */
            // 转化protobuf,放入阻塞队列
            // CachedQueue.getInstance().addIntoQueue(GetServerInfoReturn.parseFrom(buffer.clone()));
            header.setErrorCode(0);
            byte[] resp = header.toByteArray();
            out.write(resp, 0, resp.length);
            out.flush();

            StreamerClouddrmanage.GetServerInfoReturn hrt = StreamerClouddrmanage.GetServerInfoReturn.parseFrom(buffer);
            InfoProcessData process = new InfoProcessData(hrt);
            process.setRdsService(rdsService);
            process.setAlarmLogService(alarmLogService);
            process.setDataArkService(dataArkService);
            process.setOssService(ossService);
            process.setMdbService(mdbService);
            process.setEcsService(ecsService);
            process.run();

            // 清理内存
            hrt.toBuilder().clear();
            hrt.toBuilder().clearClients();
            hrt.toBuilder().clearServer();
            hrt.toBuilder().clearUuid();
            hrt.toBuilder().clearVcents();
//            hrt.toBuilder().clearClients();
//            hrt.toBuilder().clearEcsClients();
//            .....
            logger.info("Response heartbeat successfully..");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("DealSocket failed." + e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                this.socket.close();
            } catch (Exception e2) {
                logger.error(e2);
            }
        }
    }

}
