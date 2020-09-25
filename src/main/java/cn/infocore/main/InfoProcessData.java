package cn.infocore.main;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import cn.infocore.entity.*;
import lombok.Data;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;


import cn.infocore.bo.FaultSimple;
import cn.infocore.dao.AlarmLogDAO;
import cn.infocore.dto.DataArkDTO;
import cn.infocore.handler.NameHandler;
import cn.infocore.handler.User_idHandler;
import cn.infocore.protobuf.StmStreamerDrManage.Client;
import cn.infocore.protobuf.StmStreamerDrManage.ClientType;
import cn.infocore.protobuf.StmStreamerDrManage.EcsInfo;
import cn.infocore.protobuf.StmStreamerDrManage.FaultType;
import cn.infocore.protobuf.StmStreamerDrManage.GetServerInfoReturn;
import cn.infocore.protobuf.StmStreamerDrManage.MetaInfo;
import cn.infocore.protobuf.StmStreamerDrManage.OssInfo;
import cn.infocore.protobuf.StmStreamerDrManage.RdsInfo;
import cn.infocore.protobuf.StmStreamerDrManage.Streamer;
import cn.infocore.protobuf.StmStreamerDrManage.Vcent;
import cn.infocore.protobuf.StmStreamerDrManage.Vmware;
import cn.infocore.service.AlarmLogService;
import cn.infocore.service.DataArkService;
import cn.infocore.service.OssService;
import cn.infocore.service.RDSService;
import cn.infocore.service.impl.EcsService;
import cn.infocore.service.impl.MailServiceImpl;
import cn.infocore.service.impl.MdbService;
import cn.infocore.utils.MyDataSource;

//解析数据，拦截，触发报警，写数据库等操作
@Data
public class InfoProcessData {

    private static final Logger logger = Logger.getLogger(InfoProcessData.class);
    private GetServerInfoReturn hrt;
    private RDSService rdsService;
    private DataArkService dataArkService;
    private OssService ossService;
    private AlarmLogService alarmLogService;
    private EcsService ecsService;
    private MdbService mdbService;
    private List<Client_> clientList;
    private DataArkDTO data_ark;
    private List<Fault> faults;
    private List<Virtual_machine> vmList;
    private List<Vcenter> vcList;

    public InfoProcessData(GetServerInfoReturn hrt) {
        this.hrt = hrt;
    }

    public void run() throws SQLException {
        logHeartbeat(hrt);

        // 1.解析protobuf
        // 如果过来的数据方舟心跳的uuid不再内存维护链表中，扔掉....
        Set<String> uSet = DataArkList.getInstance().getData_ark_list().keySet();
        long now = System.currentTimeMillis() / 1000;
        if (uSet.contains(hrt.getUuid())) {

//            logger.debug(hrt.toString());//此注解必要时可以打开（与logHeartbeat一样的）
            // 把所有心跳过来的时间更新到HeartCache,做这个是为了检测数据方舟离线的.
            HeartCache.getInstance().addHeartCache(hrt.getUuid(), now);
            logger.info("Received to heartbeat from data ark,and data ark is on the data_ark_list,data ark uuid:"
                    + hrt.getUuid());
            // 初始化
            data_ark = new DataArkDTO();
            faults = new LinkedList<Fault>();
            clientList = new LinkedList<Client_>();
            vcList = new LinkedList<Vcenter>();
            vmList = new LinkedList<Virtual_machine>();
            LinkedList<FaultSimple> faultSimples = new LinkedList<FaultSimple>();
            parse(hrt);
            updateDataArk(data_ark);
            // 判断是否为空，避免空指针异常抛出
            if (clientList != null && clientList.size() > 0) {
                updateClient(clientList);
            }
            if (vcList != null && vcList.size() > 0) {
                updateVcenter(vcList);
            }
            if (vmList != null && vmList.size() > 0) {
                updateVirtualMachine(vmList);
            }

            Streamer dataArk = hrt.getServer();
            String dataArkId = hrt.getUuid();
            if (dataArk != null) {
                // List<FaultSimple> list = updateDataArk(dataArkId,dataArk);
            }

            List<OssInfo> ossClients = hrt.getOssClientsList();

            if (ossClients != null && !ossClients.isEmpty()) {
                List<FaultSimple> ossFaultSimples = updateOssClient(ossClients);
                faultSimples.addAll(ossFaultSimples);
            }

            List<RdsInfo> rdsInfoList = hrt.getRdsClientsList();

            List<RdsDO> rdsList = rdsService.updateRdsInfo(data_ark, rdsInfoList);
            List<RdsInstanceDO> rdsInstances = rdsService.getRDSInstanceListFromSource(data_ark, rdsInfoList);

            List<Fault> faultList = rdsService.getFault(data_ark, rdsInfoList);
            logger.info("rdsfaultsize:" + faultList.size());

            // faults.addAll(faultList);

            Map<String, FaultSimple> rdsFaultyMap = new HashMap<String, FaultSimple>();

            for (Fault fault : faultList) {
                String clientId = fault.getClient_id();
                if (rdsFaultyMap.containsKey(clientId)) {
                    int type = fault.getType();
                    FaultSimple fSimple = rdsFaultyMap.get(clientId);
                    fSimple.getFaultTypes().add(FaultType.valueOf(type));

                    String userId = fault.getUser_id();
                    if (userId != null) {
                        fSimple.getUserIds().add(userId);
                    }
                } else {

                    FaultSimple faultSimple = new FaultSimple();

                    String id = fault.getClient_id();
                    faultSimple.setTargetId(id);

                    String userId = fault.getUser_id();
                    List<String> userIds = new ArrayList<>();
                    userIds.add(userId);
                    faultSimple.setUserIds(userIds);

                    Integer clientType = fault.getClient_type();
                    if (clientType != null) {
                        faultSimple.setClientType(ClientType.valueOf(clientType));
                    }

                    int type = fault.getType();
                    List<FaultType> tFaultTypes = new ArrayList<>();
                    tFaultTypes.add(FaultType.valueOf(type));
                    faultSimple.setFaultTypes(tFaultTypes);

                    String target = fault.getTarget();
                    faultSimple.setTargetName(target);
                    rdsFaultyMap.put(clientId, faultSimple);

                }

                Set<Map.Entry<String, FaultSimple>> set = rdsFaultyMap.entrySet();
                for (Map.Entry<String, FaultSimple> faultRds : set) {
                    // logger.info("print rds info===========");
                    // logger.info(faultRds.toString());
                    faultSimples.add(faultRds.getValue());
                }
            }

            List<MetaInfo> metaInfos = hrt.getMetaClientsList();
            if (metaInfos != null && !metaInfos.isEmpty()) {
                List<FaultSimple> MetaFaultSimples = updateMetaClient(metaInfos);
                faultSimples.addAll(MetaFaultSimples);
            }
            for (MetaInfo metaInfo : metaInfos) {
                mdbService.updateMdbInfo(metaInfo);
            }
            List<EcsInfo> exEcsInfos = hrt.getEcsClientsList();
            for (EcsInfo ecsInfo : exEcsInfos) {
                ecsService.updateEcsInfo(ecsInfo);
            }
            // add dataArk info to FaultSimple
            String dataArkIp = hrt.getServer().getIp();
            String id = hrt.getUuid();
            String dataArkName = dataArkService.getDataArkNameById(id);
            for (FaultSimple faultSimple : faultSimples) {
                faultSimple.setDataArkId(dataArkId);
                faultSimple.setDataArkIp(dataArkIp);
                faultSimple.setDataArkName(dataArkName);
                faultSimple.setTimestamp(System.currentTimeMillis() / 1000);
            }
            logger.warn(faultSimples);//遍历出结果
            alarmLogService.noticeFaults(faultSimples);//这个方法里包括
            if (faults.size() > 0) {
                MailServiceImpl.getInstance().notifyCenter(data_ark, clientList, vcList, vmList, rdsList, rdsInstances,
                        faults);
            }
//            logger.debug("释放之前,notifyCenter");

            // 为什么又要释放一次
            hrt.toBuilder().clear();
            hrt.toBuilder().clearClients();
            hrt.toBuilder().clearServer();
            hrt.toBuilder().clearUuid();
            hrt.toBuilder().clearVcents();
            logger.info("Heartbeat received and parsed successfully,wait next.");
        } else {
            logger.info("The data ark uuid:" + hrt.getUuid() + " is not in Cache or Database,refused it!!!");
        }

    }

    List<FaultSimple> updateMetaClient(List<MetaInfo> metaInfo) {
        List<FaultSimple> faultSimples = mdbService.updateMetaClientList(metaInfo);
        return faultSimples;
    }

//    private List<FaultSimple> updateDataArk(String dataArkId, Streamer dataArk) {
//        // TODO Auto-generated method stub
//        return null;
//    }

    void updateDataArk(DataArkDTO data_ark) {
        dataArkService.update(data_ark);
    }

    // 更新client
    private void updateClient(List<Client_> list) {
        logger.info("Start to update client in database.");
        // Connection connection=MyDataSource.getConnection();
        QueryRunner qr = MyDataSource.getQueryRunner();
        // String sql = "update client set
        // type=?,name=?,ips=?,exceptions=?,operating_system=? where id=?";

        String sql = "update client set name=?,ips=?,exceptions=?,operating_system=? where id=?";
        int size = list.size();
        int paramSize = 0;
        for (int i = 0; i < list.size(); i++) {
            Client_ c = list.get(i);
            if (c.getIps() != null && c.getIps() != "" && c.getIps().length() > 0) {
                paramSize++;
            }
        }
        Object[][] param = new Object[paramSize][];
        Object[][] param1 = new Object[size - paramSize][];
        int j = 0;
        for (int i = 0; i < size; i++) {
            Client_ c = list.get(i);
            if (c.getIps() != null && c.getIps() != "" && c.getIps().length() > 0) {
                param[j] = new Object[5];
                // param[j][0] = c.getType();
                param[j][0] = c.getName();
                param[j][1] = c.getIps();
                param[j][2] = c.getExcept();
                param[j][3] = c.getSystem_Version();
                param[j][4] = c.getId();
                j++;
            }
        }
        int k = 0;
        for (int i = 0; i < size; i++) {
            Client_ c = list.get(i);
            if (c.getIps() == null || c.getIps() == "" || c.getIps().length() == 0) {
                param1[k] = new Object[4];
                // param1[k][0] = c.getType();
                param1[k][0] = c.getName();
                param1[k][1] = c.getExcept();
                param1[k][2] = c.getSystem_Version();
                param1[k][3] = c.getId();
                k++;
            }
        }

        try {
            qr.batch(sql, param);
            if (param1.length > 0) {
                // sql = "update client set type=?,name=?,exceptions=?,operating_system=? where
                // id=?";

                sql = "update client set name=?,exceptions=?,operating_system=? where id=?";
                qr.batch(sql, param1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // MyDataSource.close(connection);
        }
        logger.info("Client update in database successfully.");
    }

    // 更新VC
    private void updateVcenter(List<Vcenter> list) {
        logger.info("Start update VCenter..");
        QueryRunner qr = MyDataSource.getQueryRunner();
        String sql = "update vcenter set name=?,ips=?,exceptions=? where vcenter_id=?";
        int size = list.size();
        int paramSize = 0;
        for (int i = 0; i < list.size(); i++) {
            Vcenter c = list.get(i);
            if (c.getIps() != null && c.getIps() != "" && c.getIps().length() > 0) {
                paramSize++;
            }
        }
        Object[][] param = new Object[paramSize][];
        Object[][] param1 = new Object[size - paramSize][];
        int j = 0;
        for (int i = 0; i < size; i++) {
            Vcenter v = list.get(i);
            if (v.getIps() != null && v.getIps() != "" && v.getIps().length() > 0) {
                param[j] = new Object[4];
                param[j][0] = v.getName();
                param[j][1] = v.getIps();
                param[j][2] = v.getExcep();
                param[j][3] = v.getId();
                j++;
            }
        }
        int k = 0;
        for (int i = 0; i < size; i++) {
            Vcenter v = list.get(i);
            if (v.getIps() == null || v.getIps() == "" || v.getIps().length() == 0) {
                param1[k] = new Object[3];
                param1[k][0] = v.getName();
                param1[k][1] = v.getExcep();
                param1[k][2] = v.getId();
                k++;
            }
        }
        try {
            qr.batch(sql, param);
            if (param1.length > 0) {
                sql = "update vcenter set name=?,exceptions=? where vcenter_id=?";
                qr.batch(sql, param1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // MyDataSource.close(connection);
        }
        logger.info("Finished update VCenter.");
    }

    // 更新虚拟机，不管用户，只在乎是不是同样的VCenter
    private void updateVirtualMachine(List<Virtual_machine> vmlist) {
        logger.info("Start update virtual machine.");
        // Connection connection=MyDataSource.getConnection();
        QueryRunner qr = MyDataSource.getQueryRunner();
        String sql = "update vcenter_vm set name=?,path=?,exceptions=?,operating_system=? where id=?";
        int size = vmlist.size();
        Object[][] param = new Object[size][];
        for (int i = 0; i < size; i++) {
            Virtual_machine vm = vmlist.get(i);
            param[i] = new Object[5];
            param[i][0] = vm.getName();
            param[i][1] = vm.getPath();

            String vmExceptions = vm.getExcept();
            // bug#491->fixd: merge error from two tables
            if (vmExceptions != null && !vmExceptions.isEmpty()) {
                String[] exceptions = vmExceptions.split(";");
                Set<Integer> errorSet = new TreeSet<Integer>();

                for (String string : exceptions) {
                    Integer exception = Integer.getInteger(string);
                    if (exception != null) {
                        errorSet.add(exception);
                    }
                }

                List<Integer> uncheckedErrors = AlarmLogDAO.checkVmUncheckedException(vm.getId());
//                logger.debug(alarmLogManager);
//                List<Integer> uncheckedErrors = alarmLogManager.checkVmUncheckedException(vm.getId());
                if (uncheckedErrors != null && !uncheckedErrors.isEmpty()) {
                    errorSet.addAll(uncheckedErrors);
                    StringBuilder sb = new StringBuilder();
                    for (Integer error : errorSet) {
                        sb.append(error).append(";");
                    }
                    if (sb.length() > 1) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    vmExceptions = sb.toString();
                    logger.info("id: " + vm.getId() + " exceptions:" + vmExceptions);
                }
            }
            param[i][2] = vmExceptions;
            String version = "UnKnown";
            if (vm.getSystem_Version() == 0) {
                version = "Linux";
            } else if (vm.getSystem_Version() == 1) {
                version = "Windows";
            }
            param[i][3] = version;
            param[i][4] = vm.getId();
            // param[i][4]=vm.getVcenter_id();
        }
        try {
            qr.batch(sql, param);
        } catch (SQLException e) {
            logger.error("update virtual_machine failed", e);
            e.printStackTrace();
        } finally {
            // MyDataSource.close(connection);
        }
        logger.info("Finished update virtual machine in database.");
    }

    // 调试使用
    private void logHeartbeat(GetServerInfoReturn hrt) {
        logger.info("From data ark heartbeat:");
        logger.info(hrt);
    }

    // 获取对应数据方舟的名称
    private String getDataArkName(String uuid) {
        // Connection connection=MyDataSource.getConnection();
        QueryRunner q = MyDataSource.getQueryRunner();
        String sql = "select name from data_ark where id=?";
        Object[] param = {uuid};
        String name = "";
        try {
            name = q.query(sql, new NameHandler(), param);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // MyDataSource.close(connection);
        }
        return name;
    }

    // 获取数据方舟的userid
    private String getUserIdByDataArk(String uuid) {
        QueryRunner q = MyDataSource.getQueryRunner();
        Object[] param = new Object[]{uuid};
        String result = "";
        String sql = "select user_id from quota where data_ark_id=?";
        try {
            result = q.query(sql, new User_idHandler(), param);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // MyDataSource.close(connection);
        }
        return result;
    }

    // 获取普通客户端的userid
    private String getUserIdByClient(String clientId) {
        QueryRunner q = MyDataSource.getQueryRunner();
        Object[] param = new Object[]{clientId};
        String result = "";
        String sql = "select user_id from client where id=?";
        try {
            result = q.query(sql, new User_idHandler(), param);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // MyDataSource.close(connection);
        }
        return result;
    }

    // 获取vc的userid，注意vc会被不同streamer添加
    private String getUserIdByVcent(String vcId, String data_ark_id) {
        QueryRunner q = MyDataSource.getQueryRunner();
        Object[] param = new Object[]{vcId, data_ark_id};
        String result = "";
        String sql = "select user_id from vcenter where vcenter_id=? and data_ark_id=?";
        try {
            result = q.query(sql, new User_idHandler(), param);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // MyDataSource.close(connection);
        }
        return result;
    }

    private String getUserIdByVM(String uuid, String data_ark_id) {
        // Connection connection=MyDataSource.getConnection();
        QueryRunner q = MyDataSource.getQueryRunner();
        Object[] param = new Object[]{uuid, data_ark_id};
        // String sql = "select user_id from vcenter_vm where id=? and data_ark_id=?";
        String sql = "SELECT user_id from scmp.vcenter_vm as A inner join scmp.vcenter as B on A.vcenter_id=B.id and A.id =? and B.data_ark_id = ?  ";
        String result = "";
        try {
            result = q.query(sql, new User_idHandler(), param);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // MyDataSource.close(connection);
        }
        return result;
    }

    private void parse(GetServerInfoReturn hrt) {
        long now = System.currentTimeMillis() / 1000;
        this.data_ark = convertStreamerServer(hrt, now);
        convertClient(hrt, now);
        convertVCenter(hrt, now);
    }

    private List<FaultSimple> updateOssClient(List<OssInfo> ossClient) {
        List<FaultSimple> faultSimples = ossService.updateOssClientList(ossClient);
        return faultSimples;
    }

    private void convertVCenter(GetServerInfoReturn hrt, long now) {
        // 开始封装无代理客户端
        List<Vcent> vList = hrt.getVcentsList();
        if (vList != null && vList.size() > 0) {
            for (Vcent vcent : vList) {
                Vcenter vcenter = new Vcenter();
                vcenter.setId(vcent.getVcUuid());
                vcenter.setName(vcent.getVcName());
                vcenter.setIps(vcent.getVcIp());
                String user_id2 = getUserIdByVcent(vcent.getVcUuid(), data_ark.getId());
                List<Fault> v_list_faults = new LinkedList<Fault>();
                for (FaultType fault : vcent.getVcentStateList()) {
                    Fault fault2 = new Fault();
                    fault2.setTimestamp(now);
                    fault2.setUser_id(user_id2);
                    fault2.setType(fault.getNumber());
                    fault2.setData_ark_id(data_ark.getId());
                    fault2.setData_ark_name(data_ark.getName());
                    fault2.setData_ark_ip(data_ark.getIp());
                    fault2.setTarget(vcent.getVcName());
                    fault2.setClient_type(2);// 2019年3月11日18:04:13 朱伟添加
                    fault2.setClient_id(vcent.getVcUuid());// 2019年3月11日18:04:13 朱伟添加
                    v_list_faults.add(fault2);
                    faults.add(fault2);
                }
                vcenter.setFaults(v_list_faults);
                vcenter.setData_ark_id(data_ark.getId());
                vcList.add(vcenter);
                // 如果VC的异常是离线，则不用封装虚拟机以及虚拟机的异常
                boolean offline = false;
                for (FaultType ft : vcent.getVcentStateList()) {
                    if (ft == FaultType.VCENTER_OFFLINE) {
                        offline = true;
                        break;
                    }
                }
                if (offline) {
                    continue;
                }
                List<Vmware> vmwareList = convertVirtualMachine(now, vcent);
            }
        }
        // 封装结束
    }

    private List<Vmware> convertVirtualMachine(long now, Vcent vcent) {
        // 顺便封装虚拟机
        List<Vmware> vmwareList = vcent.getClientsList();
        if (vmwareList != null && vmwareList.size() > 0) {
            for (Vmware vmware : vmwareList) {
                Virtual_machine vm = new Virtual_machine();
                vm.setId(vmware.getId());
                vm.setName(vmware.getName());
                vm.setPath(vmware.getPath());
                // add by wxx 2019/05/13
                vm.setSystem_Version(vmware.getSystemVersion());
                String user_id3 = getUserIdByVM(vmware.getId(), data_ark.getId());
                List<Fault> vmware_list_faults = new LinkedList<Fault>();
                List<FaultType> vmwareStateList = vmware.getVmwareStateList();
                for (FaultType faultType : vmwareStateList) {
                    Fault fault = new Fault();
                    fault.setTimestamp(now);
                    fault.setUser_id(user_id3);
                    fault.setType(faultType.getNumber());
                    fault.setData_ark_id(data_ark.getId());
                    fault.setData_ark_name(data_ark.getName());
                    fault.setData_ark_ip(data_ark.getIp());
                    fault.setTarget(vmware.getName());
                    fault.setClient_type(3);
                    fault.setClient_id(vmware.getId());

                    if (!FaultType.VMWARE_OFFLINE.equals(faultType)) {
                        this.faults.add(fault);
                        vmware_list_faults.add(fault);
                    }
                }
                vm.setFaults(vmware_list_faults);
                vm.setVcenter_id(vcent.getVcUuid());
                vm.setData_ark_id(data_ark.getId());
                this.vmList.add(vm);
            }
        }
        // 虚拟机封装结束
        return vmwareList;
    }

    private void convertClient(GetServerInfoReturn hrt, long now) {
        // 开始封装有代理客户端Client
        List<Client> cList = hrt.getClientsList();
        if (cList != null && cList.size() > 0) {
            for (Client client : cList) {

                Client_ tmp = new Client_();
                tmp.setId(client.getId());
                tmp.setName(client.getName());
                tmp.setIps(client.getIp());
                // client.get
                // add by wxx 2019/05/13
                tmp.setSystem_Version(client.getSystemVersion());
                String user_id1 = getUserIdByClient(client.getId());
                List<Fault> client_fault_list = new LinkedList<Fault>();
                for (FaultType f : client.getClientStateList()) {
                    Fault fault = new Fault();
                    fault.setTimestamp(now);
                    fault.setUser_id(user_id1);
                    fault.setType(f.getNumber());
                    fault.setData_ark_id(data_ark.getId());
                    fault.setData_ark_name(data_ark.getName());
                    fault.setData_ark_ip(data_ark.getIp());
                    fault.setTarget(client.getName());
                    fault.setClient_type(1);// 2019年3月11日18:04:13 朱伟添加
                    fault.setClient_id(client.getId());// 2019年3月11日18:04:13 朱伟添加
                    client_fault_list.add(fault);
                    this.faults.add(fault);
                }
                tmp.setFaultList(client_fault_list);
                tmp.setType(client.getType().getNumber());
                tmp.setData_ark_id(data_ark.getId());
                this.clientList.add(tmp);
            }
        }
        // 封装有代理客户端Client完毕
    }

    private DataArkDTO convertStreamerServer(GetServerInfoReturn hrt, long now) {
        // 把心跳过来的异常信息全部先封装起来
        DataArkDTO dataServer = new DataArkDTO();
        // 开始封装Data_ark
        String uuid = hrt.getUuid();
        dataServer.setId(uuid);
        Streamer streamer = hrt.getServer();
        dataServer.setIp(streamer.getIp());
        dataServer.setName(getDataArkName(uuid));
        dataServer.setTotal_cap(streamer.getTotal());
        dataServer.setUsed_cap(streamer.getUsed());
        dataServer.setTotal_oracle_capacity(streamer.getOracleVol());
        dataServer.setTotal_rds_capacity(streamer.getRdsVol());
        long maxClient = streamer.getMaxClients();

        Long cloudVol = streamer.getCloudVol();
        Long racUsed = streamer.getRacUsed();
        Long ecsUsed = streamer.getEcsUsed();
        Long rdsUsed = streamer.getRdsUsed();
        Long ossUsed = streamer.getOssUsed();
        Long metaUsed = streamer.getMetaUsed();
        dataServer.setLimitClientCount(maxClient);
        dataServer.setCloudVol(cloudVol);
        dataServer.setRacUsed(racUsed);
        dataServer.setEcsUsed(ecsUsed);
        dataServer.setRacUsed(racUsed);
        dataServer.setRdsUsed(rdsUsed);
        dataServer.setOssUsed(ossUsed);
        dataServer.setMetaUsed(metaUsed);
        String user_id = getUserIdByDataArk(uuid);
        List<Fault> data_ark_fault_list = new LinkedList<Fault>();
        for (FaultType f : streamer.getStreamerStateList()) {
            Fault mFault = new Fault();
            mFault.setTimestamp(now);
            mFault.setUser_id(user_id);
            mFault.setType(f.getNumber());
            mFault.setData_ark_id(dataServer.getId());
            mFault.setData_ark_name(dataServer.getName());
            mFault.setData_ark_ip(dataServer.getIp());
            mFault.setTarget(dataServer.getName());
            mFault.setClient_type(0);// 2019年3月11日18:04:13 朱伟添加
            mFault.setClient_id(uuid); // add by wxx
            data_ark_fault_list.add(mFault);
            this.faults.add(mFault);
        }
        dataServer.setFaults(data_ark_fault_list);

        // Data_ark封装完毕
        return dataServer;
    }
}
