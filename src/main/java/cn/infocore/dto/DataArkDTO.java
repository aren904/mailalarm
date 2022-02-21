package cn.infocore.dto;

import java.lang.reflect.Field;
import java.util.List;

import lombok.Data;

/**
 * 数据方舟整合
 */
@Data
public class DataArkDTO {
	
    private String id;

    private String uuid;
    
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

    private int limitVcenterVmCount;

    // 数据方舟的异常 ; 分隔
    private String except;

    private List<Fault> faults;

    private int limitClientCount;

    private long cloudVol;

    private long racUsed;

    private long ecsUsed;

    private long rdsUsed;

    private long ossUsed;

    private long metaUsed;

    private long cloudUsed;

    // 数据方舟的用户id
    private String user_uuid;

    // 数据方舟的用户密码
    private String user_password;

    // 对应Data_ark_group中的id字段，是外健
    private long data_ark_group_id;

    private long total_rds_capacity;

    private int rds_endpoint_updated_version;

    private int oss_endpoint_updated_version;

    /**
     * 转字符串保存到数据库
     * @param faults
     */
    public void setFaults(List<Fault> faults) {
        this.faults = faults;
        StringBuilder string = new StringBuilder();
        for (Fault fault : faults) {
            string.append(fault.getType());
            string.append(";");
        }
        string.deleteCharAt(string.length() - 1);
        setExcept(string.toString());
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
