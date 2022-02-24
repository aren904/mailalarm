package cn.infocore.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cn.infocore.dto.ClientDTO;
import cn.infocore.dto.DataArkDTO;
import cn.infocore.dto.Fault;
import cn.infocore.dto.VCenterDTO;
import cn.infocore.dto.VirtualMachineDTO;
import cn.infocore.entity.DataArk;
import cn.infocore.service.DataArkService;
import cn.infocore.service.EmailAlarmService;
import cn.infocore.service.SnmpService;
import lombok.Data;

/**
 * 定时扫描缓存，判断数据方舟状态
 * 周期：每3分钟扫描一次，3分钟无心跳则离线（因此数据方舟离线被检测到的时间范围是0~6分钟）
 */
@Data
public class ThreadScanStreamer implements Runnable {
	
	private static final Logger logger = Logger.getLogger(ThreadScanStreamer.class);
	
	private static final long split = 3 * 60;
	
	private DataArkService dataArkService;
	
	private SnmpService mySnmpService;
	
	private EmailAlarmService emailAlarmService;
	
	private static volatile ThreadScanStreamer instance = null;
	
	public ThreadScanStreamer() {
		logger.info("ThreadScanStreamer launched.");
	}
	
	public static ThreadScanStreamer getInstance() {
    	if (instance==null) {
			synchronized (ThreadScanStreamer.class) {
                if (instance == null) {
                    instance = new ThreadScanStreamer();
                }
            }
		}
		return instance;
    }

	@Override
	public void run() {
		List<String> uuids = null;
		//获取当前数据库的数据方舟记录写入缓存，并初始化心跳时间0L
		DataArkListCache.getInstance(dataArkService);
		EmailAlarmListCache.getInstance(emailAlarmService);
		
		while (true) {
			try {
				List<DataArk> dataArks=dataArkService.list();
				logger.info("Start check data_ark state....current cache data_ark size:" + dataArks.size());
				uuids = new ArrayList<String>();
				if (dataArks.size() > 0) {
					for(DataArk dataArk:dataArks) {
						String uuid=dataArk.getUuid();
						Long updateTimestamp = dataArk.getUpdatedTimestamp();
						long now = System.currentTimeMillis() / 1000;
						if (updateTimestamp!=null&&now - updateTimestamp > split) {
							// 当前时间-最后更新的时间>3分钟,认为掉线
							logger.info("uuid:" + uuid + " is offline,update database.");
							updateOffLine(uuid, false);
							uuids.add(uuid);
						} else {
							logger.info("uuid:" + uuid + " is online,update database.");
							updateOffLine(uuid, true);
						}
					}

					if (uuids.size() > 0) {
						SnmpTrapSender sender=new SnmpTrapSender(dataArkService,mySnmpService);
						sender.run(uuids);
					}
				}

				try {
					Thread.sleep(split * 1000);
				} catch (InterruptedException e) {
					logger.error("ThreadScanStreamer interrupted...", e);
				}
			} catch (Exception e) {
				logger.error("ThreadScanStreamer:" + e);
			}
		}
	}

	/**
	 * 更新数据库中是否离线的标志
	 * @param uuid
	 * @param online true 在线 false 离线
	 */
	public void updateOffLine(String uuid, boolean online) {
		long now = System.currentTimeMillis() / 1000;
		// bug#773->solved: maintain exceptions if online
		List<String> result = findExceptionsByUuid(uuid);
		
		// 10->Streamer 服务器离线，如果在线但数据库中包含离线异常需要设置为在线；反之离线；
		if ((result != null && result.contains("10") && online) || !online) {
			dataArkService.updateDataArkStatus(uuid, online);
		}

		//对于离线的数据方舟需要邮件报警
		if (!online) {
			logger.debug("The data ark which uuid:" + uuid + " is offline...");
			
			// 如果离线，触发邮件报警
			List<Fault> data_ark_faults = new LinkedList<Fault>();
			Fault fault = new Fault();
			fault.setTimestamp(now);
			fault.setType(10); //10表示离线
			fault.setData_ark_uuid(uuid);
			fault.setClient_type(0); //0表示数据方舟
			
			try {
				DataArk dataArk=dataArkService.findByUuid(uuid);
				DataArkDTO dataArkDto=new DataArkDTO();
				if (dataArk == null) {
					logger.error("The data ark which uuid:" + uuid + " is not exist db...");
					fault.setData_ark_name("null");
					fault.setData_ark_ip("null");
					fault.setTarget_name("null");
					fault.setTarget_uuid("null");
					fault.setUser_uuid("null");
					fault.setClient_id("null");
					fault.setData_ark_uuid("null");
				} else {
					dataArkDto.setUser_uuid(dataArk.getUserUuid());
					dataArkDto.setName(dataArk.getName());
					dataArkDto.setIp(dataArk.getIp());
					dataArkDto.setUuid(dataArk.getUuid());
					
					fault.setData_ark_name(dataArkDto.getName());
					fault.setData_ark_ip(dataArkDto.getIp());
					fault.setTarget_name(dataArkDto.getName());
					fault.setTarget_uuid(dataArkDto.getUuid());
					fault.setUser_uuid(dataArkDto.getUser_uuid());
					fault.setClient_id(uuid);
					fault.setData_ark_uuid(uuid);
				}
				
				data_ark_faults.add(fault);
				//将Faults设置成Exception，这里是数据方舟离线
				dataArkDto.setFaults(data_ark_faults);
				
				//构造参数
				List<ClientDTO> clientList = new LinkedList<ClientDTO>(); //普通客户端
				List<VCenterDTO> vcList = new LinkedList<VCenterDTO>(); //VC
				List<VirtualMachineDTO> vmList = new LinkedList<VirtualMachineDTO>(); //VM
				
				//启动离线告警
				logger.debug("Start to notice fault for scan...");
				emailAlarmService.notifyCenter(dataArkDto, clientList, vcList, vmList, data_ark_faults);
			} catch (Exception e) {
				logger.warn("ThreadScanStreamer error.", e);
			}
		}
	}

	/**
	 * 获取指定数据方舟的异常
	 * @param uuid
	 * @return
	 */
	private List<String> findExceptionsByUuid(String uuid) {
		DataArk dataArk=dataArkService.findByUuid(uuid);
		if (dataArk != null) {
			String exceptions = dataArk.getExceptions();
			if (exceptions!= null && exceptions.contains(";")) {
				String[] resultArray = exceptions.split(";");
				return new ArrayList<String>(Arrays.asList(resultArray));
			} else {
				List<String> result = new ArrayList<String>();
				result.add(exceptions);
				return result;
			}
		}else {
            return new ArrayList<String>();
        }
	}
}
