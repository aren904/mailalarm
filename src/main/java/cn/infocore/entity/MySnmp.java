package cn.infocore.entity;

public class MySnmp {
    private String station_ip; //管理站IP
    private int station_port; //维护端口
    private int version; //snmp通信版本 0:V1,1:V2c,2:V3
    private String station_name;
    private String read_comm_name;//读团体名
    private String write_comm_name;//写团体名
    private long timeout_ms;//超时时间
    private int update_version;//最后一次更新版本
    private int enabled;//0表示禁用
    private String security_username;
//    Todo 下个版本需要将snmp.auth_password这些改成byte[]类型 加密字段需要用TestAesGcmAe去解密
    private int authentication_protocol;
    private int authentication_password_enabled;
    private String authentication_password;
    private int privacy_protocol;

	public int getPrivacy_protocol() {
		return privacy_protocol;
	}

	public void setPrivacy_protocol(int privacy_protocol) {
		this.privacy_protocol = privacy_protocol;
	}

	private int privacy_password_enabled;
    private String privacy_password;

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

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public String getSecurity_username() {
        return security_username;
    }

    public void setSecurity_username(String security_username) {
        this.security_username = security_username;
    }

    public int getAuthentication_protocol() {
        return authentication_protocol;
    }

    public void setAuthentication_protocol(int authentication_protocol) {
        this.authentication_protocol = authentication_protocol;
    }

    public int getAuthentication_password_enabled() {
        return authentication_password_enabled;
    }

    public void setAuthentication_password_enabled(int authentication_password_enabled) {
        this.authentication_password_enabled = authentication_password_enabled;
    }

    public String getAuthentication_password() {
        return authentication_password;
    }

    public void setAuthentication_password(String authentication_password) {
        this.authentication_password = authentication_password;
    }

    public int getPrivacy_password_enabled() {
        return privacy_password_enabled;
    }

    public void setPrivacy_password_enabled(int privacy_password_enabled) {
        this.privacy_password_enabled = privacy_password_enabled;
    }

    public String getPrivacy_password() {
        return privacy_password;
    }

    public void setPrivacy_password(String privacy_password) {
        this.privacy_password = privacy_password;
    }


    public String getRead_comm_name() {
        return read_comm_name;
    }

    public void setRead_comm_name(String read_comm_name) {
        this.read_comm_name = read_comm_name;
    }

    public String getWrite_comm_name() {
        return write_comm_name;
    }

    public void setWrite_comm_name(String write_comm_name) {
        this.write_comm_name = write_comm_name;
    }

}
