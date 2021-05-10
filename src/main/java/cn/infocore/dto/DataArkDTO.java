package cn.infocore.dto;

import java.lang.reflect.Field;
import java.util.List;

import cn.infocore.entity.Fault;

public class DataArkDTO {
    // 数据方舟id,就是Streamerid
    private String id;
    // 数据方舟名称
    private String name;
    // 数据方舟ip
    private String ip;
    // 总容量
    private long total_cap;
    // 已经使用了的容量
    private long used_cap;
    // Oracle备份空间
    private long total_oracle_capacity;

    // 数据方舟的异常 ; 分隔
    private String except;

    private List<Fault> faults;

    // 数据方舟的用户id
    private String user_id;

    // 数据方舟的用户密码
    private String user_password;

    // 对应Data_ark_group中的id字段，是外健
    private int data_ark_group_id;

    private long total_rds_capacity;
    long limitClientCount ;
    
    long cloudVol;
    
    long racUsed;
    long ecsUsed;
    long rdsUsed;
    long ossUsed;
    long metaUsed;
    
    
    
    public long getLimitClientCount() {
        return limitClientCount;
    }

    public void setLimitClientCount(long limitClientCount) {
        this.limitClientCount = limitClientCount;
    }

    public long getCloudVol() {
        return cloudVol;
    }

    public void setCloudVol(long cloudVol) {
        this.cloudVol = cloudVol;
    }

    public long getRacUsed() {
        return racUsed;
    }

    public void setRacUsed(long racUsed) {
        this.racUsed = racUsed;
    }

    public long getEcsUsed() {
        return ecsUsed;
    }

    public void setEcsUsed(long ecsUsed) {
        this.ecsUsed = ecsUsed;
    }

    public long getRdsUsed() {
        return rdsUsed;
    }

    public void setRdsUsed(long rdsUsed) {
        this.rdsUsed = rdsUsed;
    }

    public long getOssUsed() {
        return ossUsed;
    }

    public void setOssUsed(long ossUsed) {
        this.ossUsed = ossUsed;
    }



    public long getTotal_rds_capacity() {
        return total_rds_capacity;
    }

    public void setTotal_rds_capacity(long total_rds_capacity) {
        this.total_rds_capacity = total_rds_capacity;
    }

    public int getRds_endpoint_updated_version() {
        return rds_endpoint_updated_version;
    }

    public void setRds_endpoint_updated_version(int rds_endpoint_updated_version) {
        this.rds_endpoint_updated_version = rds_endpoint_updated_version;
    }

    public int getOss_endpoint_updated_version() {
        return oss_endpoint_updated_version;
    }

    public void setOss_endpoint_updated_version(int oss_endpoint_updated_version) {
        this.oss_endpoint_updated_version = oss_endpoint_updated_version;
    }

    private int rds_endpoint_updated_version;

    private int oss_endpoint_updated_version;

    public long getTotal_oracle_capacity() {
        return total_oracle_capacity;
    }

    public void setTotal_oracle_capacity(long total_oracle_capacity) {
        this.total_oracle_capacity = total_oracle_capacity;
    }

    public String getId() {
        return id;
    }

    public List<Fault> getFaults() {
        return faults;
    }

    /**
     * 转字符串保存到数据库
     * 
     * @param faults
     */
    public void setFaults(List<Fault> faults) {
        this.faults = faults;
        StringBuilder string = new StringBuilder();
        for (Fault fault : faults) {
            string.append(Integer.toString(fault.getType()));
            string.append(";");
        }
        string.deleteCharAt(string.length() - 1);
        setExcept(string.toString());
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getTotalCap() {
        return total_cap;
    }

    public void setTotal_cap(long total_cap) {
        this.total_cap = total_cap;
    }

    public long getUsed_cap() {
        return used_cap;
    }

    public void setUsed_cap(long used_cap) {
        this.used_cap = used_cap;
    }

    public String getExcept() {
        return except;
    }

    public void setExcept(String except) {
        this.except = except;
    }

    public int getData_ark_group_id() {
        return data_ark_group_id;
    }

    public void setData_ark_group_id(int data_ark_group_id) {
        this.data_ark_group_id = data_ark_group_id;
    }
    
    

    public long getMetaUsed() {
        return metaUsed;
    }

    public void setMetaUsed(long metaUsed) {
        this.metaUsed = metaUsed;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field iField : fields) {
            sb.append(iField.getName());
            sb.append("=");
            try {
                sb.append(iField.get(this));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
