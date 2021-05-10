//package cn.infocore.main;
//
//import org.snmp4j.*;
//import org.snmp4j.mp.MPv1;
//import org.snmp4j.mp.MPv2c;
//import org.snmp4j.mp.MPv3;
//import org.snmp4j.security.*;
//import org.snmp4j.smi.*;
//import org.snmp4j.transport.DefaultTcpTransportMapping;
//import org.snmp4j.transport.DefaultUdpTransportMapping;
//import org.snmp4j.util.MultiThreadedMessageDispatcher;
//import org.snmp4j.util.ThreadPool;
//import org.springframework.stereotype.Component;
//
//import java.util.Vector;
//
//public class MultiThreadedTrapReceiver implements CommandResponder {
//
//
////    private String username1 = "nmsAdminNaNp";
////    private String username2 = "nmsAdmin2ANp";
//    private String username3 = "qwe";
//    private String authPassword = "202CB962AC59075B964B07152D234B70";
//    private String privPassword = "U2FsdGVkX19VhfwRATniil0k4POwNYFC";
//
//    private MultiThreadedMessageDispatcher dispatcher;
//    private Snmp snmp = null;
//    private Address listenAddress;
//    private ThreadPool threadPool;
//
//    public MultiThreadedTrapReceiver() {
//        // BasicConfigurator.configure();
//    }
//
//    private void init() throws Exception {
//        /*threadPool = ThreadPool.create("Trap", 2);
//        dispatcher = new MultiThreadedMessageDispatcher(threadPool,
//                new MessageDispatcherImpl());
//        listenAddress = GenericAddress.parse(System.getProperty(
//                "snmp4j.listenAddress", "udp:10.195.88.96/162")); // 本地IP与监听端口
//        TransportMapping transport;
//        // 对TCP与UDP协议进行处理
//        if (listenAddress instanceof UdpAddress) {
//            transport = new DefaultUdpTransportMapping(
//                    (UdpAddress) listenAddress);
//        } else {
//            transport = new DefaultTcpTransportMapping(
//                    (TcpAddress) listenAddress);
//        }
//        snmp = new Snmp(dispatcher, transport);
//        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
//        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
//        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3());
//        UsmUser usmUser = new UsmUser(new OctetString(username), AuthMD5.ID,
//                new OctetString(authPassword), Priv3DES.ID,
//                new OctetString(privPassword));
//
//        USM usm = new USM(SecurityProtocols.getInstance().addDefaultProtocols(), new OctetString(MPv3
//                .createLocalEngineID()), 0);
//        usm.addUser(usmUser);
//        SecurityModels.getInstance().addSecurityModel(usm);
//        snmp.listen();
//
//
//
//        //add the enginID in the trap
//        //OctetString engineID = new OctetString(MPv3.createLocalEngineID());
//        byte[] enginId = "TEO_ID".getBytes();
//        OctetString engineID = new OctetString(enginId);
//        //create and add the userSecurityModel
//        USM usm = new USM(SecurityProtocols.getInstance(),engineID, 0);
//        SecurityModels.getInstance().addSecurityModel(usm);
//
//        //add the securityProtocols,you can skip it if your users are noAuthNoPriv
//        SecurityProtocols.getInstance().addDefaultProtocols();
//
//
//        //create and add the user
//        UsmUser usmUser = new UsmUser(new OctetString(username), AuthMD5.ID,
//                new OctetString(authPassword), Priv3DES.ID,
//                new OctetString(privPassword));
//        usm.addUser(usmUser);
//
//
//        //snmp.getUSM().addUser(usmUser);
//        snmp.listen();
//        */
//        /*******************************************使用处*************************************************/
//        //创建接收SnmpTrap的线程池，参数： 线程名称及线程数
//        threadPool = ThreadPool.create("Trap", 2);
//        dispatcher = new MultiThreadedMessageDispatcher(threadPool,
//                new MessageDispatcherImpl());
//        //监听端的 ip地址 和 监听端口号
////        listenAddress = GenericAddress.parse(System.getProperty(
////                "snmp4j.listenAddress", "udp:127.0.0.1/161"));
//        listenAddress = GenericAddress.parse(System.getProperty(
//                "snmp4j.listenAddress", "udp:192.168.23.223/162"));
//        TransportMapping transport;
//        if (listenAddress instanceof UdpAddress) {
//            transport = new DefaultUdpTransportMapping((UdpAddress)listenAddress);
//        }else{
//            transport = new DefaultTcpTransportMapping((TcpAddress) listenAddress);
//        }
//        snmp = new Snmp(dispatcher, transport);
////        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
////        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
//        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3());
//        //MPv3.setEnterpriseID(35904);
//        byte[] enginId = "JL-CC-SNL".getBytes();
//        USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(
//                MPv3.createLocalEngineID()),0);
//        SecurityModels.getInstance().addSecurityModel(usm);
//        // 添加安全协议,如果没有发过来的消息没有身份认证,可以跳过此段代码
//        // add all security protocols
//        SecurityProtocols.getInstance().addDefaultProtocols();
//        SecurityProtocols.getInstance().addPrivacyProtocol(new PrivDES());//此处必须添加
//        // 创建和添加用户
////        OctetString userName1 = new OctetString(username1);
////        OctetString userName2 = new OctetString(username2);
//        OctetString userName3 = new OctetString(username3);
//        OctetString authPass = new OctetString(authPassword);
//        OctetString privPass = new OctetString(privPassword);
//
//        /*UsmUser usmUser1 = new UsmUser(
//                new OctetString("nmsAdmin"),
//                AuthSHA.ID, new OctetString("nmsAuthKey"),
//                PrivAES256.ID, new OctetString("nmsPrivKey"));*/
//
////        UsmUser usmUser1 = new UsmUser(userName1, AuthMD5.ID, authPass, Priv3DES.ID, privPass);
////        UsmUser usmUser2 = new UsmUser(userName2, AuthMD5.ID, authPass, Priv3DES.ID, privPass);
//        UsmUser usmUser3 = new UsmUser(userName3, AuthMD5.ID, authPass, PrivDES.ID, privPass);
//        //因为接受的Trap可能来自不同的主机，主机的Snmp v3加密认证密码都不一样，所以根据加密的名称，来添加认证信息UsmUser。
//        //添加了加密认证信息的便可以接收来自发送端的信息。
////        UsmUserEntry userEnty1 = new UsmUserEntry(userName1,new OctetString(enginId),usmUser1);
////        UsmUserEntry userEnty2 = new UsmUserEntry(userName2,usmUser2);
////        UsmUserTable userTable = snmp.getUSM().getUserTable();
//        // 添加其他用户
////        userTable.addUser(userEnty1);
////        userTable.addUser(userEnty2);
//        //开启Snmp监听，可以接收来自Trap端的信息。+
//
////        usm.addUser(userName1,new OctetString(enginId),usmUser1);
////        usm.addUser(userName2,new OctetString(enginId),usmUser2);
//        usm.addUser(userName3,new OctetString(enginId),usmUser3);
//        snmp.listen();
//       /* UsmUserTable userTable = snmp.getUSM().getUserTable();
//        userTable.addUser(userEnty1);
//        userTable.addUser(userEnty2);*/
//        /********************************************************************************************/
//    }
//    public void run() {
//        try {
//            init();
//            snmp.addCommandResponder(this);
//            System.out.println("开始监听Trap信息!");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    /**
//     * 实现CommandResponder的processPdu方法, 用于处理传入的请求、PDU等信息
//     * 当接收到trap时，会自动进入这个方法
//     *
//     * @param respEvnt
//     */
//    @Override
//    public void processPdu(CommandResponderEvent respEvnt) {
//        // 解析Response
//        if (respEvnt != null && respEvnt.getPDU() != null) {
//            @SuppressWarnings("unchecked")
//            Vector<VariableBinding> recVBs = (Vector<VariableBinding>) respEvnt.getPDU().getVariableBindings();
//            for (int i = 0; i < recVBs.size(); i++) {
//                VariableBinding recVB = recVBs.elementAt(i);
//                System.out.println(recVB.getOid() + " : " + recVB.getVariable());
//            }
//        }
//    }
//
//    //开启监控的main方法。
//    public static void main(String[] args) {
//        MultiThreadedTrapReceiver multithreadedtrapreceiver = new MultiThreadedTrapReceiver();
//        multithreadedtrapreceiver.run();
//    }
//
//}
