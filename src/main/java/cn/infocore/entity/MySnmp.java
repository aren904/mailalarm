package cn.infocore.entity;

public class MySnmp {
	private String station_ip; //管理站IP
	private int station_port; //维护端口
	private int version; //snmp通信版本 0:V1,1:V2c,2:V3
	private String station_name;
	private String read_community_name;//读团体名
	private String write_community_name;//写团体名
	private long timeout_ms;//超时时间
	private int update_version;//最后一次更新版本
	
	public String getStation_ip() {
		return station_ip;
	}
	public void setStation_ip(String station_ip) {
		this.station_ip = station_ip;
	}
	public int getStation_port() {
		return station_port;
	}
	public void setStation_port(int station_port) {
		this.station_port = station_port;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getStation_name() {
		return station_name;
	}
	public void setStation_name(String station_name) {
		this.station_name = station_name;
	}
	public String getRead_community_name() {
		return read_community_name;
	}
	public void setRead_community_name(String read_community_name) {
		this.read_community_name = read_community_name;
	}
	public String getWrite_community_name() {
		return write_community_name;
	}
	public void setWrite_community_name(String write_community_name) {
		this.write_community_name = write_community_name;
	}
	public long getTimeout_ms() {
		return timeout_ms;
	}
	public void setTimeout_ms(long timeout_ms) {
		this.timeout_ms = timeout_ms;
	}
	public int getUpdate_version() {
		return update_version;
	}
	public void setUpdate_version(int update_version) {
		this.update_version = update_version;
	}
	
}
