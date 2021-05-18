package cn.infocore.manager;

import cn.infocore.dao.CloudMapper;
import cn.infocore.entity.CloudDo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * @ProjectName: mailalarm
 * @Package: cn.infocore.manager
 * @ClassName: CloudClientManager
 * @Author: aren904
 * @Description:
 * @Date: 2021/5/13 11:05
 * @Version: 1.0
 */
@Service
public class CloudClientManager extends ServiceImpl< CloudMapper,CloudDo> {

    @Autowired
    CloudClientManager cloudClientManager;


    public void updateCloudClient(String uuid,CloudDo cloudDo){
        LambdaQueryWrapper<CloudDo> cloudDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<CloudDo> queryWrapper = cloudDoLambdaQueryWrapper.eq(CloudDo::getUuId, uuid);
        this.baseMapper.update(cloudDo,queryWrapper);
    }


    public List<String> getUserIdByUuid(String uuid){
        LambdaQueryWrapper<CloudDo> cloudDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        cloudDoLambdaQueryWrapper.eq(CloudDo::getUuId,uuid);
        List<CloudDo> CloudDoList = this.list(cloudDoLambdaQueryWrapper);
        LinkedList<String> cloudDos = new LinkedList<>();
        if(CloudDoList!=null){
            for (CloudDo cloudDo:CloudDoList){
                String userId = cloudDo.getUserId();

                cloudDos.add(userId);
            }
        }
        return  cloudDos;
    }



    public Boolean checkCloudIsDr(CloudDo cloudDo){
        LambdaQueryWrapper<CloudDo> cloudDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        cloudDoLambdaQueryWrapper.eq(CloudDo::getUuId, cloudDo.getUuId());
        CloudDo cloudDo1 = cloudClientManager.getOne(cloudDoLambdaQueryWrapper);
        if(cloudDo1!=null){
            Integer isDr = cloudDo.getIsDr();
            return isDr!=null&& isDr >0;
        }
        return false;
    }

}
