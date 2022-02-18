package cn.infocore.dto;

import java.util.List;

import lombok.Data;

/**
 * 无代理客户端整合
 */
@Data
public class VCenterDTO {

    private String uuid;
    
    //vc的名字
    private String name;
    
    //vc的ip
    private String ips;
    
    //vc的异常
    private String exception;

    private String client_group_id;

    //对应Data_ark中的id字段，是外健
    private String data_ark_id;

    private List<Fault> faults;

    public void setFaults(List<Fault> faults) {
        this.faults = faults;
        StringBuilder string = new StringBuilder();
        for (Fault fault : faults) {
            string.append(Integer.toString(fault.getType()));
            string.append(";");
        }
        string.deleteCharAt(string.length() - 1);
        setException(string.toString());
    }

    @Override
    public String toString() {
        return "Vcenter{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", ips='" + ips + '\'' +
                ", excep='" + exception + '\'' +
                ", client_group_id='" + client_group_id + '\'' +
                ", data_ark_id='" + data_ark_id + '\'' +
                ", faults=" + faults +
                '}';
    }
}
