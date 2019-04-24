package cn.infocore.main;

import java.io.IOException;
import java.util.Vector;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;

public class MultiThreadedTrapReceiver implements CommandResponder{
	private ThreadPool threadPool;
	private Snmp snmp = null;
	private MultiThreadedMessageDispatcher dispatcher;
	private Address listenAddress;
	
	private void init() throws IOException{
		threadPool=ThreadPool.create("Trap", 2);
		dispatcher = new MultiThreadedMessageDispatcher(threadPool,new MessageDispatcherImpl());
		
		listenAddress = GenericAddress.parse(System.getProperty("snmp4j.listenAddress", "udp:192.168.23.71/163"));
		TransportMapping transport=null;
		// 对TCP与UDP协议进行处理
		if (listenAddress instanceof UdpAddress) {
			transport = new DefaultUdpTransportMapping((UdpAddress) listenAddress);
		} else {
			transport = new DefaultTcpTransportMapping((TcpAddress) listenAddress);
		}
		
		snmp = new Snmp(dispatcher, transport);
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
		snmp.listen();
	}
	
	public void processPdu(CommandResponderEvent respEvnt) {
		// 解析Response
        if (respEvnt != null && respEvnt.getPDU() != null) {
        	System.out.println(respEvnt.getPDU());
        	Vector<VariableBinding> recVBs = (Vector<VariableBinding>) respEvnt.getPDU().getVariableBindings();
           	for (int i = 0; i < recVBs.size(); i++) {
            	VariableBinding recVB = recVBs.elementAt(i);
               	System.out.println(recVB.getOid() + " : " + recVB.getVariable());
      		}
		}
	}

	public void run(){
		try {
			init();
			snmp.addCommandResponder(this);
			System.out.println("开始监听Trap信息!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		MultiThreadedTrapReceiver multithreadedtrapreceiver = new MultiThreadedTrapReceiver();
		multithreadedtrapreceiver.run();
	}
}
