package cn.infocore.test;

import StmStreamerDrManage.StreamerClouddrmanage;
import cn.infocore.operator.Header;
//import cn.infocore.protobuf.StmStreamerDrManage;


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
        StreamerClouddrmanage.GetServerInfoReturn.Builder get = StreamerClouddrmanage.GetServerInfoReturn.newBuilder();
//        get.setUuid("a5c606a6-bf23-4d84-a677-eb6990c6bda9");
        get.setUuid("b26d3b16-1195-4bc4-847a-98e7001fe609");
       StreamerClouddrmanage.Streamer.Builder s = StreamerClouddrmanage.Streamer.newBuilder();
        s.setIp("192.168.13.130");
        s.setName("localhost.localdomain");
        s.setTotal(new Long("57174596255744"));
        s.setUsed(new Long("47120136536064"));
        List<StreamerClouddrmanage.FaultType> fs = new ArrayList<>();
//        fs.add(StreamerClouddrmanage.FaultType.STREAMER_POOL_DISABLE);
        fs.add(StreamerClouddrmanage.FaultType.NORMAL);
//        fs.add( STREAMER_POOL_DISABLE);
        s.addAllStreamerState(fs);
        s.setOracleVol(new Long("1087700467712"));
        s.setMaxClients(new Long("128"));
        s.setRdsVol(0);
        s.setCloudVol(0);
        s.setRacUsed(0);
        s.setEcsUsed(0);
        s.setRdsUsed(2);
        s.setOssUsed(0);
        s.setMaxVcenterVm(128);
        s.setMetaUsed(0);
        get.setServer(s.build());

//        List<StreamerClouddrmanage.Vcent> vcs = new ArrayList<>();
//       StreamerClouddrmanage.Vcent.Builder vc = StreamerClouddrmanage.Vcent.newBuilder();
//        vc.setVcName("VMware vCenter Server 5.5.0 build-2646482");
//        vc.setVcIp("10.0.114.115");
//        vc.setVcUuid("22ba929b-b8e9-48e4-b73f-b9f5cb202169");
//        List<StreamerClouddrmanage.FaultType> fts = new ArrayList<>();
//        fts.add(StreamerClouddrmanage.FaultType.VCENTER_OFFLINE);
//        vc.addAllVcentState(fts);
//        vc.setType(StreamerClouddrmanage.ClientType.VC);
//        get.addVcents(vc);
        /**
         * required string id = 1;
         required string name = 2;
         required ClientType type = 3;
         required string path =4;
         repeated FaultType Vmware_state = 5;
         required int32 System_Version =6;
         */
            //vm
//        StreamerClouddrmanage.Vmware.Builder vm1 = StreamerClouddrmanage.Vmware.newBuilder();
//        vm1.setName("QYXY_APP_10.0.112.17");
//        vm1.setId("135a120a-8a0d-4b39-97a2-2bea111c1a67");
//        vm1.setPath("[1]c.VMware vCenter Server 5.5.0 build-2646482/");
//        vm1.setSystemVersion(1);
//        vm1.setType(StreamerClouddrmanage.ClientType.VMWARE);
//        List<StreamerClouddrmanage.FaultType> f4 = new ArrayList<>();
//        f4.add(StreamerClouddrmanage.FaultType.VMWARE_CBT_DROP);
//        vm1.addAllVmwareState(f4);
//        vc.addClients(vm1);
//        get.addVcents(vc);



        //client
        StreamerClouddrmanage.Client.Builder builder2 = StreamerClouddrmanage.Client.newBuilder();
        builder2.setName("VMware vCenter Server 6.7.0 build-8833120");
        builder2.setId("66be6134-5f96-4a6b-a67e-e761d038545a");
        builder2.setIp("192.168.11.80");
        builder2.setType(StreamerClouddrmanage.ClientType.RAC);
        builder2.setSystemVersion("linux");
        ArrayList<StreamerClouddrmanage.FaultType> f11  = new ArrayList<>();
        f11.add(StreamerClouddrmanage.FaultType.RAC_NODE_ALL_OFFLINE);
        f11.add(StreamerClouddrmanage.FaultType.RAC_INSTANCE_ALL_OFFLINE);
        builder2.addAllClientState(f11);
        get.addClients(builder2);

        //mdb
//        StmStreamerDrManage.StreamerClouddrmanage.MetaInfo.Builder builder = StmStreamerDrManage.StreamerClouddrmanage.MetaInfo.newBuilder();
//        builder.setType(StmStreamerDrManage.StreamerClouddrmanage.ClientType.MetaDB);
//        builder.setName("aren");
//        builder.setId("000f08c1-0000-0000-0000-000000000000");
//        LinkedList<StreamerClouddrmanage.FaultType> list = new LinkedList<>();
//        list.add(StreamerClouddrmanage.FaultType.META_CLIENT_OFFLINE);
//        builder.addAllStatus(list);
//
//        StmStreamerDrManage.StreamerClouddrmanage.MetaBackupInfo.Builder builder1 = builder.addBackupListBuilder();
//        builder1.setName("aren");
//        builder1.setType(StreamerClouddrmanage.ClientType.MetaDBBackup);
//        LinkedList<StreamerClouddrmanage.FaultType> faultTypes = new LinkedList<>();
//        faultTypes.add(StreamerClouddrmanage.FaultType.META_CLIENT_OFFLINE);
//        builder1.addAllStatus(faultTypes);
//
//        builder1.setSize(223);
//        builder1.setPreoccupationSizeByte(23);
//        builder1.setId("000f08c1-0000-0000-0000-000000000000");
//        get.addMetaClients(builder);
//
//



        //Oss
//        StmStreamerDrManage.OssInfo.Builder builder = StmStreamerDrManage.OssInfo.newBuilder();
//        builder.setType(StmStreamerDrManage.ClientType.Oss);
//        builder.setName("5");
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
//        builder1.setSize(99);
//        builder1.setId("c1e5edca-12c1-4b60-bec1-00a83db97214");
//        builder1.setPreoccupationSizeByte(55);
//        get.addOssClients(builder);

            //rds
//        StmStreamerDrManage.RdsInfo.Builder builder4 = StmStreamerDrManage.RdsInfo.newBuilder();
//        builder4.setType(StmStreamerDrManage.ClientType.Rds);
//        builder4.setName("555");
//        builder4.setUuid("5496868e-cdc6-4ec4-8d14-192f74ddef03");
//        LinkedList<StmStreamerDrManage.FaultType> list1 = new LinkedList<>();
//        list1.add(StmStreamerDrManage.FaultType.RDS_STORAGE_DROP);
//        builder4.addAllStatus(list1);

//        StmStreamerDrManage.RdsInstanceInfo.Builder builder5 = builder.addObjListBuilder();
//        StmStreamerDrManage.RdsInstanceInfo.Builder builder5 = builder4.addInstanceListBuilder();
//        builder5.setName("2341");
//        builder5.setType(StmStreamerDrManage.ClientType.RdsInstance);
//        LinkedList<StmStreamerDrManage.FaultType> faultTypes1 = new LinkedList<>();
//        faultTypes1.add(StmStreamerDrManage.FaultType.RDS_INSTANCE_BACKUP_POINT_DOWNLOAD_FAILED);
//        builder5.addAllStatus(faultTypes1);
//        builder5.setSize(8);
//        builder5.setUuid("5496868e-cdc6-4ec4-8d14-192f74ddef03");
//        builder5.setPreoccupationSizeByte(8);
//        get.addRdsClients(builder4);


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
        StreamerClouddrmanage.GetServerInfoReturn build = get.build();
        byte[] requestBuffer = get.build().toByteArray();
        System.out.println("requestBuffer.length:"+requestBuffer.length);

        try {
        //创建Socket对象,ip为mailalarm服务所在ip，端口23335
        Socket socket = new Socket("192.168.13.139", 23335);

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
