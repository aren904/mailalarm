package cn.infocore.main;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;
import cn.infocore.entity.Client_;
import cn.infocore.entity.Data_ark;
import cn.infocore.entity.Fault;
import cn.infocore.entity.Virtual_machine;
import cn.infocore.mail.MailCenterRestry;
import cn.infocore.protobuf.StmStreamerDrManage.Client;
import cn.infocore.protobuf.StmStreamerDrManage.FaultType;
import cn.infocore.protobuf.StmStreamerDrManage.GetServerInfoReturn;
import cn.infocore.protobuf.StmStreamerDrManage.Streamer;
import cn.infocore.protobuf.StmStreamerDrManage.Vcent;
import cn.infocore.protobuf.StmStreamerDrManage.Vmware;
import cn.infocore.utils.MyDataSource;
import cn.infocore.handler.NameHandler;

//解析数据，拦截，触发报警，写数据库等操作
public class ProcessData implements Runnable{

	private static final Logger logger=Logger.getLogger(ProcessData.class);
	private GetServerInfoReturn hrt;
	
	private List<Client_> clientList;
	private Data_ark data_ark;
	private List<Fault> faults;
	private List<Virtual_machine> vmList;
//	private List<Vcenter> vcList;
	
	public ProcessData(GetServerInfoReturn hrt) {
		this.hrt=hrt;
	}
	
	
	public void run() {
		
		logHeartbeat(hrt);
		//1.解析protobuf
		//如果过来的数据方舟心跳的uuid不再内存维护链表中，扔掉....
		Set<String> uSet=DataArkList.getInstance().getData_ark_list().keySet();
		long now = System.currentTimeMillis() / 1000;
		if (uSet.contains(hrt.getUuid())) {
			//把所有心跳过来的时间更新到HeartCache,做这个是为了检测数据方舟离线的.
			HeartCache.getInstance().addHeartCache(hrt.getUuid(), now);
			logger.info("Recived heartbeat from data ark,and data ark is on the data_ark_list,data ark uuid:"+hrt.getUuid());
			//初始化
			data_ark=new Data_ark();
			faults=new LinkedList<Fault>();
			vmList=new LinkedList<Virtual_machine>();
			parse(hrt);
			updateData_ark(data_ark);
			//判断是否为空，避免空指针异常抛出
			if (clientList!=null&&clientList.size()>0) {
				updateClient(clientList);
				updateVcenter(clientList);
			}
			if (vmList!=null&&vmList.size()>0) {
				updateVirtualMachine(vmList);
			}
			//所有异常通知邮件发送中心
			try {
				if (faults.size()>0) {
					Fault[] faults_array=new Fault[faults.size()];
					MailCenterRestry.getInstance().notifyCenter(faults.toArray(faults_array));
				}
			} catch (SQLException e) {
				logger.error(e);
			}
			logger.info("Heartbeat recived and parsed successed,wait next.");
		}else {
			logger.info("The data ark uuid:"+hrt.getUuid()+" is not in Cache or Database,refused it!!!");
		}
	}
	
	//更新data_ark
	private void updateData_ark(Data_ark data_ark) {
		logger.info("Start update data ark in database.");
		QueryRunner qr=new QueryRunner();
		String sql="update data_ark set ip=?,total_capacity=?,used_capacity=?,exceptions=?,total_oracle_capacity=? where id=?";
		Object[] param= {data_ark.getIp(),data_ark.getTotal_cap(),data_ark.getUsed_cap(),
				data_ark.getExcept(),data_ark.getTotal_oracle_capacity(),data_ark.getId()};
		Connection conn=MyDataSource.getConnection();
		try {
			qr.update(conn, sql, param);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			MyDataSource.close(conn);
		}
		logger.info("Update data ark in database finished.");
	}
	
	
	//更新client
	private void updateClient(List<Client_> list){
		logger.info("Start update client in database.");
		Connection connection=MyDataSource.getConnection();
		QueryRunner qr=new QueryRunner();
		String sql="update client set type=?,name=?,ips=?,execptions=? where id=?";
		int size=list.size();
		Object[][] param=new Object[size][];
		for (int i=0;i<size;i++) {
			Client_ c=list.get(i);
			param[i]=new Object[5];
			param[i][0]=c.getType();
			param[i][1]=c.getName();
			param[i][2]=c.getIps();
			param[i][3]=c.getExcept();
			param[i][4]=c.getId();
		}
		try {
			qr.batch(connection, sql,param);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			MyDataSource.close(connection);
		}
		logger.info("Client update in database successed.");
	}
	
	//更新VC
	private void updateVcenter(List<Client_> list) {
		logger.info("Start update VCenter..");
		Connection connection=MyDataSource.getConnection();
		QueryRunner qr=new QueryRunner();
		String sql="update vcenter set name=?,ips=?,exceptions=? where id=?";
		int size=list.size();
		Object[][] param=new Object[size][];
		for (int i=0;i<size;i++) {
			Client_ c_=list.get(i);
			param[i]=new Object[4];
			param[i][0]=c_.getName();
			param[i][1]=c_.getIps();
			param[i][2]=c_.getExcept();
			param[i][3]=c_.getId();
		}
		try {
			qr.batch(connection, sql, param);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			MyDataSource.close(connection);
		}
		logger.info("Finished update VCenter.");
	}
	
	//更新虚拟机，不管用户，只在乎是不是同样的VCenter
	private void updateVirtualMachine(List<Virtual_machine> vmlist) {
		logger.info("Start update virtual machine.");
		Connection connection=MyDataSource.getConnection();
		QueryRunner qr=new QueryRunner();
		String sql="update virtual_machine set name=?,path=?,exceptions=? where id=? and vcenter_id=?";
		int size=vmlist.size();
		Object[][] param=new Object[size][];
		for (int i=0;i<size;i++) {
			Virtual_machine vm=vmlist.get(i);
			param[i]=new Object[5];
			param[i][0]=vm.getName();
			param[i][1]=vm.getPath();
			param[i][2]=vm.getExcept();
			param[i][3]=vm.getId();
			param[i][4]=vm.getVcenter_id();
		}
		try {
			qr.batch(connection, sql,param);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			MyDataSource.close(connection);
		}
		logger.info("Finished update virtual machine in database.");
	}
	//调试使用
	private void logHeartbeat(GetServerInfoReturn hrt) {
		logger.info("From data ark heartbeat:");
		logger.info(hrt.toString());
	}
	//获取对应数据方舟的名称
	private String getDataArkNmae(String uuid) {
		Connection connection=MyDataSource.getConnection();
		QueryRunner q=new QueryRunner();
		String sql="select name from data_ark where id=?";
		Object[] param= {uuid};
		String name="";
		try {
			name=q.query(connection, sql, new NameHandler(), param);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			MyDataSource.close(connection);
		}
		return name;
	}
	
	private void parse(GetServerInfoReturn hrt){
		//把心跳过来的异常信息全部先封装起来		
		long now=System.currentTimeMillis()/1000;
		//开始封装Data_ark
		String uuid=hrt.getUuid();
		data_ark.setId(uuid);
		Streamer streamer=hrt.getServer();
		data_ark.setIp(streamer.getIp());
		data_ark.setName(getDataArkNmae(uuid));
		data_ark.setTotal_cap(streamer.getTotal());
		data_ark.setUsed_cap(streamer.getUsed());
		data_ark.setTotal_oracle_capacity(streamer.getOracleVol());
		List<Fault> data_ark_fault_list=new LinkedList<Fault>();
		for (FaultType f:streamer.getStreamerStateList()) {
			Fault mFault=new Fault();
			mFault.setTimestamp(now);
			mFault.setType(f.getNumber());
			mFault.setData_ark_id(data_ark.getId());
			mFault.setData_ark_name(data_ark.getName());
			mFault.setData_ark_ip(data_ark.getIp());
			mFault.setTarget(data_ark.getName());
			data_ark_fault_list.add(mFault);
			faults.add(mFault);
		}
		data_ark.setFaults(data_ark_fault_list);
		//Data_ark封装完毕
		
		//开始封装有代理客户端Client
		List<Client> cList=hrt.getClientsList();
		if (cList!=null&&cList.size()>0) {
			clientList=new LinkedList<Client_>();
			for (Client client:cList) {
				Client_ tmp=new Client_();
				tmp.setId(client.getId());
				tmp.setName(client.getName());
				tmp.setIps(client.getIp());
				List<Fault> client_fault_list=new LinkedList<Fault>();
				for (FaultType f:client.getClientStateList()) {
					Fault fault=new Fault();
					fault.setTimestamp(now);
					fault.setType(f.getNumber());
					fault.setData_ark_id(data_ark.getId());
					fault.setData_ark_name(data_ark.getName());
					fault.setData_ark_ip(data_ark.getIp());
					fault.setTarget(client.getName());
					client_fault_list.add(fault);
					faults.add(fault);
				}
				tmp.setfList(client_fault_list);
				tmp.setType(client.getType().getNumber());
				tmp.setData_ark_id(data_ark.getId());
				clientList.add(tmp);
			}
		}
		//封装有代理客户端Client完毕
		
		//开始封装无代理客户端
		List<Vcent> vList=hrt.getVcentsList();
		if (vList!=null&&vList.size()>0) {
			for (Vcent vcent:vList) {
				Client_ tmp=new Client_();
				tmp.setId(vcent.getVcUuid());
				tmp.setName(vcent.getVcName());
				tmp.setIps(vcent.getVcIp());
				List<Fault> v_list_faults=new LinkedList<Fault>();
				for (FaultType fault:vcent.getVcentStateList()) {
					Fault fault2=new Fault();
					fault2.setTimestamp(now);
					fault2.setType(fault.getNumber());
					fault2.setData_ark_id(data_ark.getId());
					fault2.setData_ark_name(data_ark.getName());
					fault2.setData_ark_ip(data_ark.getIp());
					fault2.setTarget(vcent.getVcName());
					v_list_faults.add(fault2);
					faults.add(fault2);
				}
				tmp.setfList(v_list_faults);
				tmp.setType(vcent.getType().getNumber());
				tmp.setData_ark_id(data_ark.getId());
				if (clientList==null) {
					clientList=new LinkedList<Client_>();
				}
				clientList.add(tmp);
				//如果VC的异常是离线，则不用封装虚拟机以及虚拟机的异常
				boolean offline=false;
				for (FaultType ft:vcent.getVcentStateList()) {
					if (ft==FaultType.VCENTER_OFFLINE) {
						offline=true;
						break;
					}
				}
				if (offline) {
					continue;
				}
				
				//顺便封装虚拟机
				List<Vmware> vmwareList=vcent.getClientsList();
				if (vmwareList!=null&&vmwareList.size()>0) {
					for (Vmware vmware:vmwareList) {
						Virtual_machine vm=new Virtual_machine();
						vm.setId(vmware.getId());
						vm.setName(vmware.getName());
						//vm.setAlias("null");
						vm.setPath(vmware.getPath());
						List<Fault> vmware_list_faults=new LinkedList<Fault>();
						for (FaultType faultType:vmware.getVmwareStateList()) {
							Fault fault=new Fault();
							fault.setTimestamp(now);
							fault.setType(faultType.getNumber());
							fault.setData_ark_id(data_ark.getId());
							fault.setData_ark_name(data_ark.getName());
							fault.setData_ark_ip(data_ark.getIp());
							fault.setTarget(vmware.getName());
							vmware_list_faults.add(fault);
							faults.add(fault);
						}
						vm.setFaults(vmware_list_faults);
						vm.setVcenter_id(vcent.getVcUuid());
						vm.setData_ark_id(data_ark.getId());
						vmList.add(vm);
					}
				}
				//虚拟机封装结束
			}
		}
		//封装结束
	}
	

}
