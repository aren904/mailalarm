package cn.infocore.main;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import cn.infocore.net.StmCommand;
import cn.infocore.net.StmHeader;
import cn.infocore.net.StmRetStatus;
import cn.infocore.protobuf.StmAlarmManage;
import cn.infocore.service.AlarmLogService;
import cn.infocore.service.ClientBackupService;
import cn.infocore.service.ClientService;
import cn.infocore.service.DataArkService;
import cn.infocore.service.MetaService;
import cn.infocore.service.OssService;
import cn.infocore.service.QuotaService;
import cn.infocore.service.RdsService;
import cn.infocore.service.UserService;
import cn.infocore.service.impl.EcsServiceImpl;
import cn.infocore.utils.Utils;
import lombok.Data;

/**
 * 解析心跳header
 */
@Data
public class DealSocket implements Runnable {
	
	private static final Logger logger = Logger.getLogger(DealSocket.class);
    
    private Socket socket;
    
    private RdsService rdsService;
    
    private EcsServiceImpl ecsService;
    
    private MetaService metaService;
    
    private DataArkService dataArkService;
    
    private OssService ossService;
    
    private AlarmLogService alarmLogService;
    
    private ClientService clientService;
    
    private UserService userService;
    
    private QuotaService quotaService;
    
    private ClientBackupService clientBackupService;
    
    /**
     * 构造失败响应头
     * @return
     */
    public StmHeader ResponseHeader() {
        StmHeader header = new StmHeader();
        header.setVersion((byte) 1);
        header.setDataType((byte) 2);
        header.setErrorCode(StmRetStatus.ST_RES_FAILED);
        header.setFlags((short) 0);
        header.setFrom((short) 25);
        header.setCommand(StmCommand.ST_OP_MANAGEMENT_HEARTBEAT);
        header.setDataLength(0);
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
            
            byte[] headerBuffer = new byte[StmHeader.STREAMER_HEADER_LENGTH];
            ioret = in.read(headerBuffer, 0, StmHeader.STREAMER_HEADER_LENGTH);
            if (ioret != StmHeader.STREAMER_HEADER_LENGTH) {
                logger.error(Utils.fmt("Failed to receive header,[%d] byte(s) expected,but [%d] is received.",
                        StmHeader.STREAMER_HEADER_LENGTH, ioret));
                
                StmHeader header = ResponseHeader();
                byte[] resp = header.toByteArray();
                out.write(resp, 0, resp.length);
                out.flush();
                throw new Exception();
            }

            StmHeader header = new StmHeader();
            header.parseByteArray(headerBuffer);

            if (header.getCommand() != StmCommand.ST_OP_MANAGEMENT_HEARTBEAT) {
            	logger.error(Utils.fmt("Incorrect command for heartbeat."));
            	header.setFrom((short) 25);
            	header.setErrorCode(StmRetStatus.ST_RES_FAILED);
                header.setDataLength(0);
                byte[] resp = header.toByteArray();
                out.write(resp, 0, resp.length);
                out.flush();
                throw new Exception();
            }

            byte[] buffer = new byte[header.getDataLength()];
            ioret = in.read(buffer, 0, buffer.length);
            if (ioret != buffer.length) {
            	logger.error(Utils.fmt("Failed to receive protobuf buffer, [%d] byte(s) expected, but [%d] byte(s) received.",
            			header.getDataLength(), ioret));
            }
            
            logger.info("Received heartbeat from osnstm.");
            header.setErrorCode(StmRetStatus.ST_RES_SUCCESS);
            header.setVersion((byte)1);
            header.setFrom((short) 25);
        	header.setDataLength(0);
            byte[] resp = header.toByteArray();
            out.write(resp, 0, resp.length);
            out.flush();

            StmAlarmManage.GetServerInfoReturn hrt = StmAlarmManage.GetServerInfoReturn.parseFrom(buffer);
            InfoProcessData process = new InfoProcessData(hrt);
            process.setRdsService(rdsService);
            process.setAlarmLogService(alarmLogService);
            process.setDataArkService(dataArkService);
            process.setOssService(ossService);
            process.setMetaService(metaService);
            process.setEcsService(ecsService);
            process.setClientService(clientService);
            process.setUserService(userService);
            process.setQuotaService(quotaService);
            process.setClientBackupService(clientBackupService);
            process.run();

            // 清理内存??为什么需要清理，待定
            hrt.toBuilder().clear();
            hrt.toBuilder().clearClients();
            hrt.toBuilder().clearServer();
            hrt.toBuilder().clearUuid();
            hrt.toBuilder().clearVcents();
            logger.debug("Response heartbeat successfully..");
        } catch (Exception e) {
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
