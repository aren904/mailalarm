package cn.infocore.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.infocore.operator.Header;
import cn.infocore.protobuf.StmStreamerDrManage;
import cn.infocore.protobuf.StmStreamerDrManage.ClientType;
import cn.infocore.protobuf.StmStreamerDrManage.FaultType;
import cn.infocore.protobuf.StmStreamerDrManage.GetServerInfoReturn;
import cn.infocore.protobuf.StmStreamerDrManage.Streamer;
import cn.infocore.protobuf.StmStreamerDrManage.Vcent;
import cn.infocore.protobuf.StmStreamerDrManage.Vmware;

import static cn.infocore.protobuf.StmStreamerDrManage.FaultType.*;

public class Test {
    public static void main(String[] args) throws SQLException {
        GetServerInfoReturn.Builder get = GetServerInfoReturn.newBuilder();
//        get.setUuid("c1e5edca-12c1-4b60-bec1-00a83db972141");
        get.setUuid("a5c606a6-bf23-4d84-a677-eb6990c6bda9");
        Streamer.Builder s = Streamer.newBuilder();
        s.setIp("192.168.13.130");
        s.setName("localhost.localdomain");
        s.setTotal(new Long("57174596255744"));
        s.setUsed(new Long("47120136536064"));
        List<FaultType> fs = new ArrayList<>();
        fs.add(FaultType.NORMAL);
        s.addAllStreamerState(fs);
        s.setOracleVol(new Long("1087700467712"));
        s.setMaxClients(new Long("128"));
        s.setRdsVol(3);
        s.setCloudVol(0);
        s.setRacUsed(0);
        s.setEcsUsed(0);
        s.setRdsUsed(0);
        s.setOssUsed(0);
        s.setMetaUsed(0);
        get.setServer(s.build());

//        List<Vcent> vcs = new ArrayList<>();
//        Vcent.Builder vc = Vcent.newBuilder();
//        vc.setVcName("VMware vCenter Server 5.5.0 build-2646482");
//        vc.setVcIp("10.0.114.115");
//        vc.setVcUuid("8CEA50E4-9871-417C-8C6B-4C76EE795D31");
//        List<FaultType> fts = new ArrayList<>();
//        fts.add(FaultType.NORMAL);
//        vc.addAllVcentState(fts);
//        vc.setType(ClientType.VC);
        /**
         * required string id = 1;
         required string name = 2;
         required ClientType type = 3;
         required string path =4;
         repeated FaultType Vmware_state = 5;
         required int32 System_Version =6;
         */

        StmStreamerDrManage.Client.Builder builder = StmStreamerDrManage.Client.newBuilder();
        builder.setName("z2y");
        builder.setId("283");
        builder.setIp("192.168.11.80");
        builder.setType(ClientType.SINGLE);
        List<FaultType> f11 = new ArrayList<>();
        f11.add(FaultType.NORMAL);
        builder.addAllClientState(f11);
        builder.setSystemVersion("Centos Linux release 8.0.1905(core)");
        get.addClients(builder);

        StmStreamerDrManage.EcsInfo.Builder builder1 = StmStreamerDrManage.EcsInfo.newBuilder();
        builder1.setName("101");
        builder1.setId("6eed0467-409b-4426-b582-b1c5aa830e95");
        builder1.setType(ClientType.Ecs);
        ArrayList<FaultType> f12 = new ArrayList<>();
        f12.add(ECS_CLIENT_OFFLINE);
        builder1.addAllStatus(f12);
        StmStreamerDrManage.EcsInstanceInfo.Builder builder2 = builder1.addInstanceListBuilder();
        builder2.setName("test");
        builder2.setId("a413067f-01b4-45b6-b8b7-624853b04085");
        builder2.setType(ClientType.EcsInstance);
        ArrayList<FaultType> list = new ArrayList<>();
        list.add(ECS_BACKUP_DST_LOST);
        builder2.addAllStatus(list);
        builder2.setSize(0);
        builder2.setPreoccupationSizeByte(0);
        get.addEcsClients(builder1);
//

//        StmStreamerDrManage.RdsInfo.Builder builder1 = StmStreamerDrManage.RdsInfo.newBuilder();
//        builder1.setName("101");
//        builder1.setUuid("6eed0467-409b-4426-b582-b1c5aa830e95");
//        builder1.setType(ClientType.Rds);
//        ArrayList<FaultType> f12 = new ArrayList<>();
//        f12.add(RDS_CLIENT_OFFLINE);
//        builder1.addAllStatus(f12);
//
//        StmStreamerDrManage.RdsInstanceInfo.Builder builder2=builder1.addInstanceListBuilder();
//        builder2.setName("test");
//        builder2.setUuid("a413067f-01b4-45b6-b8b7-624853b04085");
//        builder2.setType(ClientType.RdsInstance);
//        ArrayList<FaultType> list = new ArrayList<>();
//        list.add(RDS_BACKUP_SERVICE_OFFLINE);
//        builder2.addAllStatus(list);
//        builder2.setSize(0);
//        builder2.setPreoccupationSizeByte(0);
//        get.addRdsClients(builder1);

//        get.addMetaClients(builder1);
//        Vmware.Builder vm1 = Vmware.newBuilder();
//        vm1.setName("QYXY_APP_10.0.112.17");
//        vm1.setId("50353408-e86c-2e48-6ae8-b125adb5721a");
//        vm1.setPath("[1]c.VMware vCenter Server 5.5.0 build-2646482/[2]vCenter_PD_DMZ/[4]DMZ_Cluster_\\346\\211\\230\\347\\256\\241\\345\\214\\272/[3]10.0.114.127/[5]\\346\\250\\252\\347\\220\\264\\344\\274\\201\\344\\270\\232\\344\\277\\241\\346\\201\\257\\357\\274\\210\\344\\277\\241\\347\\224\\250\\347\\275\\221\\357\\274\\211");
//        vm1.setSystemVersion(1);
//        vm1.setType(ClientType.VMWARE);
//        List<FaultType> f4 = new ArrayList<>();
//        f4.add(FaultType.VMWARE_CREATE_SNAP_FAILED);
//        vm1.addAllVmwareState(f4);
//        vc.addClients(vm1);
//
//        Vmware.Builder vm2 = Vmware.newBuilder();
//        vm2.setName("DZZBAPP51_10.0.112.186");
//        vm2.setId("502c1817-471a-41d9-b4a1-dc1052e96d88");
//        vm2.setPath("[1]VMware vCenter Server 5.5.0 build-2646482/[2]vCenter_PD_DMZ/[4]DMZ_Cluster_\\346\\211\\230\\347\\256\\241\\345\\214\\272/[3]10.0.114.127/[5]\\347\\224\\265\\345\\255\\220\\346\\213\\233\\346\\240\\207\\346\\255\\243\\345\\274\\217\\347\\216\\257\\345\\242\\2030316");
//        vm2.setSystemVersion(0);
//        vm2.setType(ClientType.VMWARE);
//        List<FaultType> f5 = new ArrayList<>();
//        f5.add(FaultType.NORMAL);
//        vm2.addAllVmwareState(f5);
//        vc.addClients(vm2);
//
//        Vmware.Builder vm3 = Vmware.newBuilder();
//        vm3.setName("QYXY_DB_10.0.112.18");
//        vm3.setId("503584f5-e77b-f8d3-6e52-593046df3cf2");
//        vm3.setPath("[1]VMware vCenter Server 5.5.0 build-2646482/[2]vCenter_PD_DMZ/[4]DMZ_Cluster_\\346\\211\\230\\347\\256\\241\\345\\214\\272/[3]10.0.114.127/[5]\\346\\250\\252\\347\\220\\264\\344\\274\\201\\344\\270\\232\\344\\277\\241\\346\\201\\257\\357\\274\\210\\344\\277\\241\\347\\224\\250\\347\\275\\221\\357\\274\\211");
//        vm3.setSystemVersion(0);
//        vm3.setType(ClientType.VMWARE);
//        List<FaultType> f6 = new ArrayList<>();
//        f6.add(FaultType.NORMAL);
//        vm3.addAllVmwareState(f6);
//        vc.addClients(vm3);
//
//        Vmware.Builder vm5 = Vmware.newBuilder();
//        vm5.setName("fawen_10.0.115.23");
//        vm5.setId("502cef73-ec38-2caa-11c3-5f3d7a02376b");
//        vm5.setPath("[1]VMware vCenter Server 5.5.0 build-2646482/[2]vCenter_PD_DMZ/[4]DMZ_Cluster_\\346\\211\\230\\347\\256\\241\\345\\214\\272/[3]10.0.114.127/[5]\\345\\217\\221\\346\\226\\207\\347\\274\\226\\345\\217\\267\\347\\256\\241\\347\\220\\206");
//        vm5.setSystemVersion(1);
//        vm5.setType(ClientType.VMWARE);
//        List<FaultType> f8 = new ArrayList<>();
//        f8.add(FaultType.NORMAL);
//        vm5.addAllVmwareState(f8);
//        vc.addClients(vm5);
////        vcs.add(vc.build());
//
//        Vmware.Builder vm6 = Vmware.newBuilder();
//        vm6.setName("NCCWAPP-10.0.115.32");
//        vm6.setId("502c62e7-4f38-3479-5d6d-abd138c962aa");
//        vm6.setPath("[1]VMware vCenter Server 5.5.0 build-2646482/[2]vCenter_PD_DMZ/[4]DMZ_Cluster_\\346\\211\\230\\347\\256\\241\\345\\214\\272/[3]10.0.114.127/[5]\\351\\233\\206\\345\\233\\242\\350\\264\\242\\345\\212\\241\\347\\224\\250\\345\\217\\213NC6\\346\\265\\213\\350\\257\\225");
//        vm6.setSystemVersion(1);
//        vm6.setType(ClientType.VMWARE);
//        List<FaultType> f9 = new ArrayList<>();
//        f9.add(FaultType.NORMAL);
//        vm6.addAllVmwareState(f9);
//        vc.addClients(vm6);
////        vcs.add(vc.build());
//
//        Vmware.Builder vm7 = Vmware.newBuilder();
//        vm7.setName("CYXM-10.0.112.169");
//        vm7.setId("502cdc1b-0597-7166-2a33-00f4c43e91ad");
//        vm7.setPath("[1]VMware vCenter Server 5.5.0 build-2646482/[2]vCenter_PD_DMZ/[4]DMZ_Cluster_\\346\\211\\230\\347\\256\\241\\345\\214\\272/[3]10.0.114.127/[5]\\345\\273\\272\\350\\256\\276\\347\\216\\257\\344\\277\\235\\345\\261\\200");
//        vm7.setSystemVersion(1);
//        vm7.setType(ClientType.VMWARE);
//        List<FaultType> f10 = new ArrayList<>();
//        f10.add(FaultType.NORMAL);
//        vm7.addAllVmwareState(f10);
//        vc.addClients(vm7);


//        StmStreamerDrManage.Client.Builder builder = StmStreamerDrManage.Client.newBuilder();
//        builder.setName("zdy");
//        builder.setId("3");
//        builder.setType(ClientType.SINGLE);
//        List<FaultType> f11 = new ArrayList<>();
//        f11.add(FaultType.META_AUTH_ABNORMAL);
//        builder.addAllClientState(f11);
//        get.addClients(builder);


//        vcs.add(vc.build());
//        get.addAllVcents(vcs);
        System.out.println(get);


        byte[] requestBuffer = get.build().toByteArray();

        try {
            //创建Socket对象
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
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
