package cn.infocore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class MdbDO {

    Integer id;

    String uuid;

    String userId;

    String dataArkId;

    Integer type;

    String name;

    Integer isDr;

    String clientGroupId;

    String ips;

    String operateSystem;

    String exceptions;

}
