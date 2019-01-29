package cn.infocore.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import cn.infocore.entity.Client_;
import cn.infocore.entity.Data_ark;
import cn.infocore.entity.Fault;
import cn.infocore.entity.Virtual_machine;
import cn.infocore.mail.MailCenterRestry;
import cn.infocore.protobuf.StmStreamerDrManage.Client;
import cn.infocore.protobuf.StmStreamerDrManage.ClientType;
import cn.infocore.protobuf.StmStreamerDrManage.FaultType;
import cn.infocore.protobuf.StmStreamerDrManage.GetServerInfoReturn;
import cn.infocore.protobuf.StmStreamerDrManage.Streamer;
import cn.infocore.protobuf.StmStreamerDrManage.Vcent;
import cn.infocore.protobuf.StmStreamerDrManage.Vmware;
import cn.infocore.utils.DBUtils;
import cn.infocore.utils.MyDataSource;

//解析数据，拦截，触发报警，写数据库等操作
public class ProcessData implements Runnable{

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
		//1.解析protobuf
		//如果过来的数据方舟心跳的uuid不再内存维护链表中，扔掉....
		Set<String> uSet=DataArkList.getInstance().getData_ark_list().keySet();
		if (uSet.contains(hrt.getUuid())) {
			//Streamer掉线处理，单独拿出来
			//StreamerQueue.getInstance().addIntoQueue(hrt.getUuid(), System.currentTimeMillis()/1000);
			//初始化
			data_ark=new Data_ark();
			faults=new LinkedList<Fault>();
			vmList=new LinkedList<Virtual_machine>();
			parse(hrt);
			updateData_ark(data_ark);
			updateClient(clientList);
			updateVcenter(clientList);
			updateVirtualMachine(vmList);
			
			//2.拦截，报警过滤,查询数据库，如果数据库中存在这条错误，
			//则检查是否勾选多久发送一次，当前的时间-上次更新时间
			try {
				Fault[] faults_array=new Fault[faults.size()];
				MailCenterRestry.getInstance().notifyCenter(faults.toArray(faults_array));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	//更新data_ark
	private void updateData_ark(Data_ark data_ark) {
		String sql="update data_ark set name=?,ips=?,total_capacity=?,used_capacity=?,exceptions=? where id=?";
		Object[] param= {data_ark.getName(),data_ark.getIp(),data_ark.getTotal_cap(),data_ark.getUsed_cap(),
				data_ark.getExcept(),data_ark.getId()};
		Connection conn=MyDataSource.getConnection();
		DBUtils.executUpdate(conn, sql, param);
	}
	
	
	//更新client
	private void updateClient(List<Client_> list){
		/*批量更新
		 * UPDATE categories SET
    			display_order = CASE id
        			WHEN 1 THEN 3
        			WHEN 2 THEN 4
        			WHEN 3 THEN 5
    			END,
    			title = CASE id
        			WHEN 1 THEN 'New Title 1'
        			WHEN 2 THEN 'New Title 2'
        			WHEN 3 THEN 'New Title 3'
    			END
			WHERE id IN (1,2,3)
		 */
		Connection connection=MyDataSource.getConnection();
		PreparedStatement statement=null;
		try {
			boolean auto=connection.getAutoCommit();
			connection.setAutoCommit(false);
			String sql="update client set type=?,name=?,ips=?,execeptions=? where id=?";
			statement=connection.prepareStatement(sql);
			for (Client_ client_:list) {
				statement.setObject(1, client_.getType());
				statement.setObject(2, client_.getName());
				statement.setObject(3, client_.getIps());
				statement.setObject(4, client_.getExcept());
				statement.setObject(5, client_.getId());
				//statement.setObject(6, client_.getData_ark_id());
				statement.addBatch();
			}
			statement.executeBatch();
			connection.commit();
			connection.setAutoCommit(auto);
		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}finally {
			if (connection!=null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	//更新VC
	private void updateVcenter(List<Client_> list) {
		Connection connection=MyDataSource.getConnection();
		PreparedStatement statement=null;
		try {
			boolean auto=connection.getAutoCommit();
			connection.setAutoCommit(false);
			String sql="update vcenter set name=?,ips=?,exceptions=? where id=?";
			statement=connection.prepareStatement(sql);
			for (Client_ client_:list) {
				if (client_.getType()==ClientType.VC) {
					statement.setObject(1, client_.getName());
					statement.setObject(2, client_.getIps());
					statement.setObject(3, client_.getExcept());
					statement.setObject(4, client_.getId());
					statement.addBatch();
				}
			}
			statement.executeBatch();
			connection.commit();
			connection.setAutoCommit(auto);
		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}finally {
			if (connection!=null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
	}
	
	//更新虚拟机，不管用户，只在乎是不是同样的VCenter
	private void updateVirtualMachine(List<Virtual_machine> vmlist) {
		Connection connection=MyDataSource.getConnection();
		PreparedStatement statement=null;
		try {
			boolean auto=connection.getAutoCommit();
			connection.setAutoCommit(false);
			String sql="update virtual_machine set name=?,path=?,exceptions=? where id=? and vcenter_id=?";
			statement=connection.prepareStatement(sql);
			for (Virtual_machine vm:vmlist) {
				statement.setObject(1, vm.getName());
				statement.setObject(2, vm.getPath());
				statement.setObject(3, vm.getExcept());
				statement.setObject(4, vm.getId());
				statement.setObject(5, vm.getVcenter_id());
				statement.addBatch();
			}
			statement.executeBatch();
			connection.commit();
			connection.setAutoCommit(auto);
		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}finally {
			if (connection!=null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	private void parse(GetServerInfoReturn hrt){
		//把心跳过来的异常信息全部先封装起来		
		long now=System.currentTimeMillis()/1000;
		//开始封装Data_ark
		data_ark.setId(hrt.getUuid());
		Streamer streamer=hrt.getServer();
		//data_ark.setName();
		data_ark.setIp(streamer.getIp());
		data_ark.setName(streamer.getName());
		data_ark.setTotal_cap(streamer.getTotal());
		data_ark.setUsed_cap(streamer.getUsed());
		List<Fault> data_ark_fault_list=new LinkedList<Fault>();
		for (FaultType f:streamer.getStreamerStateList()) {
			Fault mFault=new Fault();
			mFault.setTimestamp(now);
			mFault.setType(f);
			mFault.setData_ark_id(data_ark.getId());
			mFault.setData_ark_name(data_ark.getName());
			mFault.setData_ark_ip(data_ark.getIp());
			mFault.setTarget(data_ark.getName());
			data_ark_fault_list.add(mFault);
			faults.add(mFault);
		}
		data_ark.setFaults(data_ark_fault_list);
		data_ark.setUpdate_timestamp(now);
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
					fault.setType(f);
					fault.setData_ark_id(data_ark.getId());
					fault.setData_ark_name(data_ark.getName());
					fault.setData_ark_ip(data_ark.getIp());
					fault.setTarget(client.getName());
					client_fault_list.add(fault);
					faults.add(fault);
				}
				tmp.setfList(client_fault_list);
				tmp.setType(client.getType());
				tmp.setData_ark_id(data_ark.getId());
				clientList.add(tmp);
			}
		}
		//封装有代理客户端Client完毕
		
		//开始封装无代理客户端
		List<Vcent> vList=hrt.getVcentsList();
		for (Vcent vcent:vList) {
			Client_ tmp=new Client_();
			tmp.setId(vcent.getVcUuid());
			tmp.setName(vcent.getVcName());
			tmp.setIps(vcent.getVcIp());
			List<Fault> v_list_faults=new LinkedList<Fault>();
			for (FaultType fault:vcent.getVcentStateList()) {
				Fault fault2=new Fault();
				fault2.setTimestamp(now);
				fault2.setType(fault);
				fault2.setData_ark_id(data_ark.getId());
				fault2.setData_ark_name(data_ark.getName());
				fault2.setData_ark_ip(data_ark.getIp());
				fault2.setTarget(vcent.getVcName());
				v_list_faults.add(fault2);
				faults.add(fault2);
			}
			tmp.setfList(v_list_faults);
			tmp.setType(vcent.getType());
			tmp.setData_ark_id(data_ark.getId());
			clientList.add(tmp);
			//顺便封装虚拟机
			for (Vmware vmware:vcent.getClientsList()) {
				Virtual_machine vm=new Virtual_machine();
				vm.setId(vmware.getId());
				vm.setName(vmware.getName());
				//vm.setAlias("null");
				vm.setPath(vmware.getPath());
				List<Fault> vmware_list_faults=new LinkedList<Fault>();
				for (FaultType faultType:vmware.getVmwareStateList()) {
					Fault fault=new Fault();
					fault.setTimestamp(now);
					fault.setType(faultType);
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
			//虚拟机封装结束
		}
		//封装结束
	}
	

}
