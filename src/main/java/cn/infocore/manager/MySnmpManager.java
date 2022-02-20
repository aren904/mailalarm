package cn.infocore.manager;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.entity.MySnmp;
import cn.infocore.mapper.MySnmpMapper;

@Component
public class MySnmpManager extends ServiceImpl<MySnmpMapper,MySnmp> {
    
}
