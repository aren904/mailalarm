package cn.infocore.test;

import cn.infocore.operator.Header;
import cn.infocore.protobuf.StmStreamerDrManage;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @ProjectName: mailalarm
 * @Package: cn.infocore.test
 * @ClassName: TestIsUpdate
 * @Author: aren904
 * @Description: 测试客户端是否更新
 * @Date: 2021/5/14 16:26
 * @Version: 1.0
 */
public class TestIsUpdate {
    public static void main(String[] args) {
        StmStreamerDrManage.GetServerInfoReturn.Builder get = StmStreamerDrManage.GetServerInfoReturn.newBuilder();
        get.setUuid("a5c606a6-bf23-4d84-a677-eb6990c6bda9");
        StmStreamerDrManage.Streamer.Builder s = StmStreamerDrManage.Streamer.newBuilder();
        s.setIp("192.168.13.130");
        s.setName("localhost.localdomain");
        s.setTotal(new Long("57174596255744"));
        s.setUsed(new Long("47120136536064"));
        List<StmStreamerDrManage.FaultType> fs = new ArrayList<>();
        fs.add(StmStreamerDrManage.FaultType.NORMAL);
        s.addAllStreamerState(fs);
        s.setOracleVol(new Long("1087700467712"));
        s.setMaxClients(new Long("128"));
        s.setRdsVol(3);
        s.setCloudVol(0);
        s.setRacUsed(0);
        s.setEcsUsed(0);
        s.setRdsUsed(2);
        s.setOssUsed(0);
        s.setMetaUsed(0);
        get.setServer(s.build());

        List<StmStreamerDrManage.Vcent> vcs = new ArrayList<>();
        StmStreamerDrManage.Vcent.Builder vc = StmStreamerDrManage.Vcent.newBuilder();
        vc.setVcName("VMware vCenter Server 5.5.0 build-2646482");
        vc.setVcIp("10.0.114.115");
        vc.setVcUuid("a5c606a6-bf23-4d84-a677-eb6990c6bda9");
        List<StmStreamerDrManage.FaultType> fts = new ArrayList<>();
        fts.add(StmStreamerDrManage.FaultType.VCENTER_OFFLINE);
        vc.addAllVcentState(fts);
        vc.setType(StmStreamerDrManage.ClientType.VC);
        get.addVcents(vc);
        /**
         * required string id = 1;
         required string name = 2;
         required ClientType type = 3;
         required string path =4;
         repeated FaultType Vmware_state = 5;
         required int32 System_Version =6;
         */
            //vm
//        StmStreamerDrManage.Vmware.Builder vm1 = StmStreamerDrManage.Vmware.newBuilder();
//        vm1.setName("QYXY_APP_10.0.112.17");
//        vm1.setId("a5c606a6-bf23-4d84-a677-eb6990c6bda9");
//        vm1.setPath("[1]c.VMware vCenter Server 5.5.0 build-2646482/");
//        vm1.setSystemVersion(1);
//        vm1.setType(StmStreamerDrManage.ClientType.VMWARE);
//        List<StmStreamerDrManage.FaultType> f4 = new ArrayList<>();
//        f4.add(StmStreamerDrManage.FaultType.VMWARE_CREATE_SNAP_FAILED);
//        vm1.addAllVmwareState(f4);
//        vc.addClients(vm1);
//        get.addVcents(vc);



        //client
        StmStreamerDrManage.Client.Builder builder2 = StmStreamerDrManage.Client.newBuilder();
        builder2.setName("z2y");
        builder2.setId("a5c606a6-bf23-4d84-a677-eb6990c6bda9");
        builder2.setIp("192.168.11.80");
        builder2.setType(StmStreamerDrManage.ClientType.SINGLE);
        builder2.setSystemVersion("linux");
        ArrayList<StmStreamerDrManage.FaultType> f11  = new ArrayList<>();
        f11.add(StmStreamerDrManage.FaultType.CLIENT_SNAP_MERGE_FAILED);
        builder2.addAllClientState(f11);
        get.addClients(builder2);

        //mdb
//        StmStreamerDrManage.MetaInfo.Builder builder = StmStreamerDrManage.MetaInfo.newBuilder();
//        builder.setType(StmStreamerDrManage.ClientType.MetaDB);
//        builder.setName("555");
//        builder.setId("a5c606a6-bf23-4d84-a677-eb6990c6bda9");
//        LinkedList<StmStreamerDrManage.FaultType> list = new LinkedList<>();
//        list.add(StmStreamerDrManage.FaultType.META_CLIENT_OFFLINE);
//        builder.addAllStatus(list);
//
//        StmStreamerDrManage.MetaBackupInfo.Builder builder1 = builder.addBackupListBuilder();
//        builder1.setName("1");
//        builder1.setType(StmStreamerDrManage.ClientType.MetaDBBackup);
//        LinkedList<StmStreamerDrManage.FaultType> faultTypes = new LinkedList<>();
//        faultTypes.add(StmStreamerDrManage.FaultType.META_AUTH_ABNORMAL);
//        builder1.addAllStatus(faultTypes);
//        builder1.setSize(0);
//        builder1.setId("a5c606a6-bf23-4d84-a677-eb6990c6bda9");
//        builder1.setPreoccupationSizeByte(0);
//        get.addMetaClients(builder);




        //Oss
//        StmStreamerDrManage.OssInfo.Builder builder = StmStreamerDrManage.OssInfo.newBuilder();
//        builder.setType(StmStreamerDrManage.ClientType.Oss);
//        builder.setName("555");
//        builder.setUuid("a5c606a6-bf23-4d84-a677-eb6990c6bda9");
//        LinkedList<StmStreamerDrManage.FaultType> list = new LinkedList<>();
//        list.add(StmStreamerDrManage.FaultType.OSS_CLIENT_OFFLINE);
//        builder.addAllStatus(list);
//
//        StmStreamerDrManage.OssObjectSetInfo.Builder builder1 = builder.addObjListBuilder();
//        builder1.setName("234");
//        builder1.setType(StmStreamerDrManage.ClientType.OssObjectSet);
//        LinkedList<StmStreamerDrManage.FaultType> faultTypes = new LinkedList<>();
//        faultTypes.add(StmStreamerDrManage.FaultType.OSS_BACKUP_DST_LOST);
//        builder1.addAllStatus(faultTypes);
//        builder1.setSize(0);
//        builder1.setId("c1e5edca-12c1-4b60-bec1-00a83db97214");
//        builder1.setPreoccupationSizeByte(0);
//        get.addOssClients(builder);

            //rds
//        StmStreamerDrManage.RdsInfo.Builder builder = StmStreamerDrManage.RdsInfo.newBuilder();
//        builder.setType(StmStreamerDrManage.ClientType.Rds);
//        builder.setName("555");
//        builder.setUuid("a5c606a6-bf23-4d84-a677-eb6990c6bda9");
//        LinkedList<StmStreamerDrManage.FaultType> list = new LinkedList<>();
//        list.add(StmStreamerDrManage.FaultType.RDS_STORAGE_DROP);
//        builder.addAllStatus(list);
//
//        StmStreamerDrManage.RdsInstanceInfo.Builder builder1 = builder.addInstanceListBuilder();
//        builder1.setName("2341");
//        builder1.setType(StmStreamerDrManage.ClientType.RdsInstance);
//        LinkedList<StmStreamerDrManage.FaultType> faultTypes = new LinkedList<>();
//        faultTypes.add(StmStreamerDrManage.FaultType.RDS_INSTANCE_BACKUP_POINT_DOWNLOAD_FAILED);
//        builder1.addAllStatus(faultTypes);
//        builder1.setSize(0);
//        builder1.setUuid("c1e5edca-12c1-4b60-bec1-00a83db97214");
//        builder1.setPreoccupationSizeByte(0);
//        get.addRdsClients(builder);


        //Ecs
//        StmStreamerDrManage.EcsInfo.Builder builder = StmStreamerDrManage.EcsInfo.newBuilder();
//        builder.setType(StmStreamerDrManage.ClientType.Ecs);
//        builder.setName("555");
//        builder.setId("a5c606a6-bf23-4d84-a677-eb6990c6bda9");
//        LinkedList<StmStreamerDrManage.FaultType> list = new LinkedList<>();
//        list.add(StmStreamerDrManage.FaultType.RDS_STORAGE_DROP);
//        builder.addAllStatus(list);
//
//        StmStreamerDrManage.EcsInstanceInfo.Builder builder1 = builder.addInstanceListBuilder();
//        builder1.setName("2341");
//        builder1.setType(StmStreamerDrManage.ClientType.RdsInstance);
//        LinkedList<StmStreamerDrManage.FaultType> faultTypes = new LinkedList<>();
//        faultTypes.add(StmStreamerDrManage.FaultType.RDS_INSTANCE_BACKUP_POINT_DOWNLOAD_FAILED);
//        builder1.addAllStatus(faultTypes);
//        builder1.setSize(0);
//        builder1.setId("c1e5edca-12c1-4b60-bec1-00a83db97214");
//        builder1.setPreoccupationSizeByte(0);
//        get.addEcsClients(builder);

        System.out.println(get);

    byte[] requestBuffer = get.build().toByteArray();

        try {
        //创建Socket对象,ip为mailalarm服务所在ip，端口23335
        Socket socket = new Socket("192.168.13.130", 23335);

        //根据输入输出流和服务端连接
        OutputStream out = socket.getOutputStream();//获取一个输出流，向服务端发送信息
        InputStream sis = socket.getInputStream();

        Header header = new Header();
        header.setCommand(87000);
        header.setVersion((short) 1);
        header.setFlags((short) 0);
        header.setDataType((short) 0);
        header.setDataLength(requestBuffer.length);
        header.setErrorCode(1);
        header.setDirection((short) 0);

        System.out.println(header.getDataLength() + "----datalength");

        byte[] headerBuffer = header.toByteArrayLittle();  //模拟C发送消息，因为代码解析那边按照C解析的
        out.write(headerBuffer, 0, headerBuffer.length);

        if (requestBuffer != null) {
            out.write(requestBuffer, 0, requestBuffer.length);
        }

        byte[] headerBuffer1 = new byte[Header.STREAMER_HEADER_LENGTH];
        int ioret = sis.read(headerBuffer1, 0, headerBuffer1.length);
        if (ioret != Header.STREAMER_HEADER_LENGTH) {
            System.out.println("error headerLength!");
        }
        Header header1 = new Header();
        header1.parseByteArray(headerBuffer1);
        System.out.println(header1.getCommand() + "," + header1.getErrorCode());

        out.close();
        socket.close();
    } catch (
    UnknownHostException e) {
        e.printStackTrace();
    } catch (
    IOException e) {
        e.printStackTrace();
    }
}


}
