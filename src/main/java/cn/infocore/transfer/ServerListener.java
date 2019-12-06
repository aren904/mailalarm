package cn.infocore.transfer;

public interface ServerListener {
	String serverName = "ServerListener";
	
	boolean select();
	
	void handle();
}
