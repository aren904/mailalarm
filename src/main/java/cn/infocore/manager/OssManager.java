package cn.infocore.manager;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.dao.OssMapper;
import cn.infocore.entity.OssDO;
@Component
public class OssManager extends ServiceImpl<OssMapper, OssDO>{

    
    public List<String>  getOssUserIdsById(String id){
        LambdaQueryWrapper<OssDO> lambdaQueryWrapper = new  LambdaQueryWrapper<OssDO>();
        lambdaQueryWrapper.eq(OssDO::getId,id );
        LinkedList<String> userIdList= new LinkedList<String>();
        
        Collection<OssDO> ossDOs =  this.list(lambdaQueryWrapper);
        for (OssDO ossDO : ossDOs) {
            userIdList.add(ossDO.getUserId());
        }
        
        return userIdList;
    }
    

}
