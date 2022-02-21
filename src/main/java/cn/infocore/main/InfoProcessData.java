package cn.infocore.main;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import cn.infocore.dto.ClientDTO;
import cn.infocore.dto.DataArkDTO;
import cn.infocore.dto.Fault;
import cn.infocore.dto.FaultDTO;
import cn.infocore.dto.VCenterDTO;
import cn.infocore.dto.VirtualMachineDTO;
import cn.infocore.entity.DataArk;
import cn.infocore.entity.Quota;
import cn.infocore.entity.User;
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
import cn.infocore.service.impl.EmailAlarmServiceImpl;
import lombok.Data;

/**
 * 解析心跳数据
 */
@Data
public class InfoProcessData {
	
    private static final Logger logger = Logger.getLogger(InfoProcessData.class);

    private StmAlarmManage.GetServerInfoReturn hrt;
    
    private RdsService rdsService;
    
    private DataArkService dataArkService;
    
    private OssService ossService;
    
    private AlarmLogService alarmLogService;
    
    private EcsServiceImpl ecsService;
    
    private MetaService metaService;
    
    private ClientService clientService;
    
    private UserService userService;
    
    private QuotaService quotaService;
    
    private ClientBackupService clientBackupService;
    
    private List<ClientDTO> clientList;
    
    private DataArkDTO data_ark;
    
    private List<Fault> faults;
    
    private List<VirtualMachineDTO> vmList;
    
    private List<VCenterDTO> vcList;
    
    public InfoProcessData(StmAlarmManage.GetServerInfoReturn hrt) {
        this.hrt = hrt;
    }
    
    public void run() throws SQLException {
        logHeartbeat(hrt);

        // 获取DataArkList缓存的链表<uuid,ip>
        Set<String> uSet = DataArkListCache.getInstance(dataArkService).getData_ark_list().keySet();
        logger.debug("Current cache with uuid,ip in DataArkList:" + uSet);
        
        long now = System.currentTimeMillis() / 1000;
        if (uSet.contains(hrt.getUuid())) {
            logger.debug("Received to heartbeat from osnstm,uuid:"+hrt.getUuid()+" is in DataArkList cache.");

            // 把所有心跳过来的时间更新到HeartCache,做这个是为了检测数据方舟离线的.
            HeartCache.getInstance().addHeartCache(hrt.getUuid(), now);
            
            // 初始化
            data_ark = new DataArkDTO();
            //数据方舟，有代理客户端，VC,VM的异常
            faults = new LinkedList<Fault>();
            clientList = new LinkedList<ClientDTO>();
            vcList = new LinkedList<VCenterDTO>();
            vmList = new LinkedList<VirtualMachineDTO>();
            //收集OSS,RDS,META,ECS客户端的异常
            LinkedList<FaultDTO> faultDtos = new LinkedList<FaultDTO>();
            
            //解析心跳信息
            parse(hrt);
            //更新数据方舟
            dataArkService.update(data_ark);

            //更新客户端与虚拟机
            if (clientList != null && clientList.size() > 0) {
                clientService.updateClient(clientList);
            }
            
            if (vcList != null && vcList.size() > 0) {
            	clientService.updateVCenter(vcList);
            }
            
            if (vmList != null && vmList.size() > 0) {
            	clientBackupService.updateVirtualMachine(vmList);
            }

            //更新OSS客户端
            List<StmAlarmManage.OssInfo> ossClients = hrt.getOssClientsList();
            if (ossClients != null) {
	            for (StmAlarmManage.OssInfo ossClient : ossClients) {
	            	ossService.updateOssClient(ossClient);
	            }
            }
            
            //收集OSS客户端和备份关系的异常
            if (ossClients != null && !ossClients.isEmpty()) {
            	List<FaultDTO> ossFaults = ossService.findFaultFromOssClients(ossClients);
            	faultDtos.addAll(ossFaults);
            }

            //更新RDS客户端
            List<StmAlarmManage.RdsInfo> rdsClients = hrt.getRdsClientsList();
            if (rdsClients != null) {
                for (StmAlarmManage.RdsInfo rdsClient : rdsClients) {
                	rdsService.updateRdsClient(rdsClient);
                }
            }
            
            //收集RDS客户端和实例的异常
            if (rdsClients != null && !rdsClients.isEmpty()) {
                List<FaultDTO> rdsFaults = rdsService.findFaultFromRdsClients(rdsClients);
                faultDtos.addAll(rdsFaults);
            }

            //更新元数据客户端
            List<StmAlarmManage.MetaInfo> metaClients = hrt.getMetaClientsList();
            if (metaClients != null) {
                for (StmAlarmManage.MetaInfo metaClient : metaClients) {
                	metaService.updateMetaClient(metaClient);
                }
            }
            
            //收集元数据客户端和备份关系的异常
            if (metaClients != null && !metaClients.isEmpty()) {
            	List<FaultDTO> metaFaults = metaService.findFaultFromMetaClients(metaClients);
                faultDtos.addAll(metaFaults);
            }

            //更新ECS客户端
            List<StmAlarmManage.EcsInfo> ecsClients = hrt.getEcsClientsList();
            if (ecsClients != null) {
                for (StmAlarmManage.EcsInfo ecsClient : ecsClients) {
                	ecsService.updateEcsClient(ecsClient);
                }
            }
            
            //收集ECS客户端和备份关系的异常
            if (ecsClients != null && !ecsClients.isEmpty()) {
            	List<FaultDTO> ecsFaults = ecsService.findFaultFromEcsClients(ecsClients);
                faultDtos.addAll(ecsFaults);
            }

            for (FaultDTO fault : faultDtos) {
            	fault.setDataArkUuid(hrt.getUuid());
            	fault.setDataArkIp(hrt.getServer().getIp());
            	fault.setDataArkName(hrt.getServer().getName());
            	fault.setTimestamp(System.currentTimeMillis() / 1000);
            }
            alarmLogService.noticeFaults(faultDtos);

            if (faults.size() > 0) {
            	//启动邮件报警
                EmailAlarmServiceImpl.getInstance().notifyCenter(data_ark, clientList, vcList, vmList, faults);
            }

            // 为什么又要释放一次
            hrt.toBuilder().clear();
            hrt.toBuilder().clearClients();
            hrt.toBuilder().clearServer();
            hrt.toBuilder().clearUuid();
            hrt.toBuilder().clearVcents();
            logger.info("Heartbeat received and parsed successfully,wait next.");
        } else {
            logger.info("The data ark uuid:" + hrt.getUuid() + " is not in Cache or Database or delete force,refused it!!!");
        }
    }

    // 调试使用
    private void logHeartbeat(StmAlarmManage.GetServerInfoReturn hrt) {
    	logger.debug("-------Received heartbeat info from osnstm-------");
        logger.debug(hrt);
    }

    /**
     * 解析心跳
     * @param hrt
     */
    private void parse(StmAlarmManage.GetServerInfoReturn hrt) {
        try {
			long now = System.currentTimeMillis() / 1000;
			this.data_ark = convertStreamer(hrt, now);//封装数据方舟
			convertClient(hrt, now);//封装Client信息（把接收到hdr中的内容一一赋值给对象）
			convertVCenter(hrt, now); //封装Vcenter信息
		} catch (Exception e) {
			logger.error("Failed to parse heartbeat.",e);
		}
    }

    /**
     * 封装VCenter
     * @param hrt
     * @param now
     */
    private void convertVCenter(StmAlarmManage.GetServerInfoReturn hrt, long now) {
        List<StmAlarmManage.Vcent> vList = hrt.getVcentsList();
        if (vList != null && vList.size() > 0) {
            for (StmAlarmManage.Vcent vcent : vList) {
                VCenterDTO vcenter = new VCenterDTO();
                vcenter.setUuid(vcent.getVcUuid());
                vcenter.setName(vcent.getVcName());
                vcenter.setIps(vcent.getVcIp());

                List<Fault> v_list_faults = new LinkedList<Fault>();
                List<Long> user_ids=clientService.findUserIdsByUuid(vcent.getVcUuid());
                for(Long user_id:user_ids) {
                	String user_uuid=userService.findById(user_id).getUuid();
                    for (StmAlarmManage.FaultType f : vcent.getVcentStateList()) {
                        Fault fault = new Fault();
                        fault.setTimestamp(now);
                        fault.setUser_uuid(user_uuid);
                        fault.setType(f.getNumber());
                        fault.setData_ark_uuid(data_ark.getUuid());
                        fault.setData_ark_name(data_ark.getName());
                        fault.setData_ark_ip(data_ark.getIp());
                        fault.setTarget_name(vcent.getVcName());
                        fault.setClient_type(2);
                        fault.setClient_id(vcent.getVcUuid());
                        v_list_faults.add(fault);
                        faults.add(fault);
                    }
                }
                
                vcenter.setFaults(v_list_faults);
                vcenter.setData_ark_id(data_ark.getUuid());
                vcList.add(vcenter);
                
                // 如果VC的异常是离线，则不用封装虚拟机以及虚拟机的异常
                boolean offline = false;
                for (StmAlarmManage.FaultType ft : vcent.getVcentStateList()) {
                    if (ft == StmAlarmManage.FaultType.VCENTER_OFFLINE) {
                        offline = true;
                        break;
                    }
                }

                if (offline) {
                    continue;
                }
                
                convertVirtualMachine(now, vcent);
            }
        }
    }
    
    /**
     * 封装虚拟机
     * @param now
     * @param vcent
     * @return
     */
    private List<StmAlarmManage.Vmware> convertVirtualMachine(long now, StmAlarmManage.Vcent vcent) {
        List<StmAlarmManage.Vmware> vmwareList = vcent.getClientsList();
        if (vmwareList != null && vmwareList.size() > 0) {
            for (StmAlarmManage.Vmware vmware : vmwareList) {
                VirtualMachineDTO vm = new VirtualMachineDTO();
                vm.setUuid(vmware.getId()); //对应的是uuid
                vm.setName(vmware.getName());
                vm.setPath(vmware.getPath());
                vm.setSystem_Version(vmware.getSystemVersion());

                List<Fault> vmware_list_faults = new LinkedList<Fault>();
                List<Long> user_ids=clientService.findUserIdsByUuid(vmware.getId());
                for(Long user_id:user_ids) {
                	String user_uuid=userService.findById(user_id).getUuid();
                	List<StmAlarmManage.FaultType> vmwareStateList = vmware.getVmwareStateList();
                    for (StmAlarmManage.FaultType faultType : vmwareStateList) {
                        Fault fault = new Fault();
                        fault.setTimestamp(now);
                        fault.setUser_uuid(user_uuid );
                        fault.setType(faultType.getNumber());
                        fault.setData_ark_uuid(data_ark.getUuid());
                        fault.setData_ark_name(data_ark.getName());
                        fault.setData_ark_ip(data_ark.getIp());
                        fault.setTarget_name(vmware.getName());
                        fault.setClient_type(3);
                        fault.setClient_id(vmware.getId());

                        this.faults.add(fault);
                        vmware_list_faults.add(fault);
                    }
                }
                
                vm.setFaults(vmware_list_faults);
                vm.setVcenter_id(vcent.getVcUuid());
                vm.setData_ark_id(data_ark.getUuid());
                this.vmList.add(vm);
            }
        }
        return vmwareList;
    }

    /**
     * 封装有代理客户端
     * @param hrt
     * @param now
     */
    private void convertClient(StmAlarmManage.GetServerInfoReturn hrt, long now) {
        List<StmAlarmManage.Client> cList = hrt.getClientsList();
        
        if (cList != null && cList.size() > 0) {
            for (StmAlarmManage.Client client : cList) {
                ClientDTO tmp = new ClientDTO();
                tmp.setUuid(client.getId()); //协议里传递的其实是uuid
                tmp.setName(client.getName());
                tmp.setIps(client.getIp());
                tmp.setSystem_Version(client.getSystemVersion());
                
                List<Long> user_ids=clientService.findUserIdsByUuid(client.getId());
                List<Fault> client_fault_list = new LinkedList<Fault>();
                for(Long user_id:user_ids) {
                	String user_uuid=userService.findById(user_id).getUuid();
                	for (StmAlarmManage.FaultType f : client.getClientStateList()) {
                        Fault fault = new Fault();
                        fault.setTimestamp(now);
                        fault.setUser_uuid(user_uuid);
                        fault.setType(f.getNumber());
                        fault.setData_ark_uuid(data_ark.getUuid());
                        fault.setData_ark_name(data_ark.getName());
                        fault.setData_ark_ip(data_ark.getIp());
                        fault.setTarget_name(client.getName());
                        fault.setClient_type(1);
                        fault.setClient_id(client.getId());
                        client_fault_list.add(fault);
                        this.faults.add(fault);
                    }
                }
                
                tmp.setFaultList(client_fault_list);
                tmp.setType(client.getType().getNumber());
                tmp.setData_ark_id(data_ark.getUuid());
                this.clientList.add(tmp);
            }
        }
    }
    
    /**
     * 封装数据方舟对象
     * @param hrt
     * @param now
     * @return
     */
    private DataArkDTO convertStreamer(StmAlarmManage.GetServerInfoReturn hrt, long now) {
        // 把心跳过来的异常信息全部先封装起来
        DataArkDTO dataServer = new DataArkDTO();
        
        String uuid = hrt.getUuid();
        dataServer.setUuid(uuid);
        
        DataArk dataArk=dataArkService.findByUuid(uuid);
        StmAlarmManage.Streamer streamer = hrt.getServer();
        dataServer.setIp(streamer.getIp());
        dataServer.setName(dataArk==null?"":dataArk.getName());
        dataServer.setTotal_cap(streamer.getTotal());
        dataServer.setUsed_cap(streamer.getUsed());
        dataServer.setTotal_oracle_capacity(streamer.getOracleVol());
        dataServer.setTotal_rds_capacity(streamer.getRdsVol());
        
        //配额
        Long racUsed = streamer.getRacUsed();
        Long ecsUsed = streamer.getEcsUsed();
        Long rdsUsed = streamer.getRdsUsed();
        Long ossUsed = streamer.getOssUsed();
        Long metaUsed = streamer.getMetaUsed();
        Long cloudUsed = ecsUsed+rdsUsed+ossUsed+metaUsed;
        dataServer.setLimitClientCount((int)streamer.getMaxClients());
        dataServer.setCloudVol(streamer.getCloudVol());
        dataServer.setCloudUsed(cloudUsed);
        dataServer.setRacUsed(racUsed);
        dataServer.setEcsUsed(ecsUsed);
        dataServer.setRacUsed(racUsed);
        dataServer.setRdsUsed(rdsUsed);
        dataServer.setOssUsed(ossUsed);
        dataServer.setMetaUsed(metaUsed);
        dataServer.setLimitVcenterVmCount((int)streamer.getMaxVcenterVm());

        //心跳过来的异常：每一个拥有该数据方舟的用户都需要构造Fault
        if(dataArk!=null) {
        	List<Quota> quotas=quotaService.findByDataArkId(dataArk.getId());
        	for(Quota quota:quotas) {
        		User user = userService.findById(quota.getUser_id());
        		if(user!=null) {
        			List<Fault> data_ark_fault_list = new LinkedList<Fault>();
        			//注意这里收集了所有状态封装到Fault，包括在线
                    for (StmAlarmManage.FaultType f : streamer.getStreamerStateList()) {
                        Fault mFault = new Fault();
                        mFault.setTimestamp(now);
                        mFault.setUser_uuid(user.getUuid());
                        mFault.setType(f.getNumber());
                        mFault.setData_ark_uuid(dataServer.getUuid());
                        mFault.setData_ark_name(dataServer.getName());
                        mFault.setData_ark_ip(dataServer.getIp());
                        mFault.setTarget_name(dataServer.getName());
                        mFault.setClient_type(0);
                        mFault.setClient_id(uuid);
                        data_ark_fault_list.add(mFault);
                        this.faults.add(mFault);
                    }
                    dataServer.setFaults(data_ark_fault_list);
        		}
        	}
        }
        return dataServer;
    }

}
