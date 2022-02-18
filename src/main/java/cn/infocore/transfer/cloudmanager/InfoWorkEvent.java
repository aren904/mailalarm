package cn.infocore.transfer.cloudmanager;

import cn.infocore.transfer.ServerListener;
import cn.infocore.transfer.WorkEventGroup;

public class InfoWorkEvent implements WorkEventGroup{
	
	ServerListener listener;

	@Override
	public boolean select() {
		listener.select();
		return false;
	}
}
