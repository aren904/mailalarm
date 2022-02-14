package cn.infocore.entity;

/**
 * 用户ID和数据方舟ID
 */
public class Quota {
	private String user_id;
	private String data_ark_id;
	private String client_id;
	private String vcenter_vm_count;
	private Long block;
	private Long oracle;
	private Long ecs;
	private Long oss;
	private Long rds;
	private Long mdb;

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public String getVcenter_vm_count() {
		return vcenter_vm_count;
	}

	public void setVcenter_vm_count(String vcenter_vm_count) {
		this.vcenter_vm_count = vcenter_vm_count;
	}

	public Long getBlock() {
		return block;
	}

	public void setBlock(Long block) {
		this.block = block;
	}

	public Long getOracle() {
		return oracle;
	}

	public void setOracle(Long oracle) {
		this.oracle = oracle;
	}

	public Long getEcs() {
		return ecs;
	}

	public void setEcs(Long ecs) {
		this.ecs = ecs;
	}

	public Long getOss() {
		return oss;
	}

	public void setOss(Long oss) {
		this.oss = oss;
	}

	public Long getRds() {
		return rds;
	}

	public void setRds(Long rds) {
		this.rds = rds;
	}

	public Long getMdb() {
		return mdb;
	}

	public void setMdb(Long mdb) {
		this.mdb = mdb;
	}

	//	private Long oracle;
	public String getUser_id() {
		return user_id;
	}
	
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	
	public String getData_ark_id() {
		return data_ark_id;
	}
	
	public void setData_ark_id(String data_ark_id) {
		this.data_ark_id = data_ark_id;
	}
	
}
