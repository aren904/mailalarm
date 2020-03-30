package cn.infocore.manager;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.dao.VcenterMapper;
import cn.infocore.entity.VcenterDO;
@Component
public class VcenterManager extends ServiceImpl<VcenterMapper, VcenterDO>  {
    
    public List<String> getUserIdListByVcenterDbId(Integer dbId){
        LambdaQueryWrapper<VcenterDO> lambdaQueryWrapper = new LambdaQueryWrapper<VcenterDO>();
        lambdaQueryWrapper.eq(VcenterDO::getId,dbId );
        List<VcenterDO> vCenterDOList = this.list(lambdaQueryWrapper);
        List<String>  userIdList = new ArrayList<String>();
        for (VcenterDO vcenterDO : vCenterDOList) {
            String userId =  vcenterDO.getUserId();
            if (userId!= null) {
                userIdList.add(userId);
            }
            
        }
        return userIdList;
    }

}
