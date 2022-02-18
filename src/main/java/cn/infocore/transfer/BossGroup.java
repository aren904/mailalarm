package cn.infocore.transfer;

import java.util.Iterator;
import java.util.LinkedList;

public class BossGroup {
	
	volatile boolean stop;
	
	LinkedList<WorkEventGroup> workServiceList;

	protected void doSelect(LinkedList<WorkEventGroup> workServiceList) {
		while (!stop && Thread.currentThread().isInterrupted()) {
			for (Iterator<WorkEventGroup> iterator = workServiceList.iterator(); iterator.hasNext();) {
				WorkEventGroup workService = (WorkEventGroup) iterator.next();
				workService.select();
			}
		}
	}

	boolean interupt() {
		boolean prevStop = this.stop;
		this.stop = true;
		return prevStop;
	}

	public LinkedList<WorkEventGroup> getWorkServiceList() {
		return workServiceList;
	}

	public void setWorkServiceList(LinkedList<WorkEventGroup> workServiceList) {
		this.workServiceList = workServiceList;
	}

}
