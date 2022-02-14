package cn.infocore.utils;


//import ch.qos.logback.core.net.SocketConnector;

import org.apache.log4j.Logger;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;


public class ObtainIpFromItself {
    private static final Logger logger = Logger.getLogger(ObtainIpFromItself.class.getName());
//    public static void main(String[] args) {
//
//
//
//
//    }


    public static String getIp() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        logger.debug("Local HostAddress: " + addr.getHostAddress());
        String hostname = addr.getHostName();
        logger.debug("Local host name: " + hostname);
        return addr.getHostAddress();
    }


    public static String getCurrentIp() {
//        try {
//            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
//            while (networkInterfaces.hasMoreElements()) {
//                NetworkInterface ni = (NetworkInterface) networkInterfaces.nextElement();
//                Enumeration<InetAddress> nias = ni.getInetAddresses();
//                while (nias.hasMoreElements()) {
//                    InetAddress ia = (InetAddress) nias.nextElement();
//                    if (!ia.isLinkLocalAddress() && !ia.isLoopbackAddress() && ia instanceof Inet4Address) {
//                        logger.info(ia.toString());
//                    return ia.toString();
//                    }
//                }
//            }
//        } catch (SocketException e) {
//            System.out.println("Fail to get currentIp.");
//        }
//        return null;

//        String ip = "";
//        try {
//            Enumeration<?> e1 = NetworkInterface.getNetworkInterfaces();//获取多个网卡
//            while (e1.hasMoreElements()) {
//                NetworkInterface ni = (NetworkInterface) e1.nextElement();
//
//                if (("eth0").equals(ni.getName()) || ("ens192").equals(ni.getName())) {//取“eth0”和“ens33”两个网卡
//                    Enumeration<?> e2 = ni.getInetAddresses();
//                    while (e2.hasMoreElements()) {
//                        InetAddress ia = (InetAddress) e2.nextElement();
//                        if (ia instanceof Inet4Address) {//排除IPv6地址
//                            continue;
//                        }
//                        ip = ia.getHostAddress();
//                    }
//                    break;
//                }
//            }
//        } catch (SocketException e) {
//            e.printStackTrace();
//        }
//        return ip;
//    }

//        Enumeration<NetworkInterface> n;
//        try {
//            n = NetworkInterface.getNetworkInterfaces();
//            for (; n.hasMoreElements(); ) {
//                NetworkInterface e = n.nextElement();
//                Enumeration<InetAddress> a = e.getInetAddresses();
//                for (; a.hasMoreElements(); ) {
//                    InetAddress addr = a.nextElement();
//                    String ipAddress = addr.getHostAddress();
//                    if (!ipAddress.equals("127.0.0.1")
//                            && ipAddress.indexOf(":") == -1) {
//                        return ipAddress;
//                    }
//                }
//            }
//            throw new RuntimeException(
//                    "Can't get the current host ip address.");
//        } catch (SocketException e1) {
//            throw new RuntimeException(
//                    "Can't get the current host ip address:" + e1);
//        }
//    }

        return null;
    }


    public static List<InterfaceAddress> getInterfaceAddresses() {
        List<InterfaceAddress> result = new ArrayList<>();
        try {
            // 拿到所有的网卡
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
//            NetworkInterface ens192 = NetworkInterface.getByName("ens192");
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();
                if (ni.isLoopback() || ni.isPointToPoint() || ni.isVirtual()) {
                    // 特殊网卡不处理
                    continue;
                }
//                // 收集上网卡上配置的所有地址，包括v4和v6
                List<InterfaceAddress> interfaceAddresses = ni.getInterfaceAddresses();
                for (Iterator iterator = interfaceAddresses.iterator(); iterator.hasNext(); ) {
                    InterfaceAddress interfaceAddress = (InterfaceAddress) iterator.next();
                    if (interfaceAddress.getAddress().isLinkLocalAddress()) {
                        //本地链接地址不处理
                        iterator.remove();
                    }
                }
                if ("ens192".equals(ni.getName())) {
                    result.addAll(interfaceAddresses);
                }
            }
        } catch (SocketException e) {
//            SocketConnector.ExceptionHandler.handle(log, new RestRuntimeException(2301, e.getMessage(), e));
            e.printStackTrace();
        }
        return result;
    }
}
//    }


//            String localIP = "127.0.0.1";
//            try {
//                OK:
//                for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements(); ) {
//                    NetworkInterface networkInterface = interfaces.nextElement();
//                    if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp()) {
//                        continue;
//                    }
//                    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
//                    while (addresses.hasMoreElements()) {
//                        InetAddress address = addresses.nextElement();
//                        if (address instanceof Inet4Address) {
//                            localIP = address.getHostAddress();
//                            break OK;
//                        }
//                    }
//                }
//            } catch (SocketException e) {
//                e.printStackTrace();
//            }
//            return localIP;
//        }

//    }


