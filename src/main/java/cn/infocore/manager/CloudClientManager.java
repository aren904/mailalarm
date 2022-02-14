//package cn.infocore.manager;
//
//import cn.infocore.dao.CloudMapper;
//import cn.infocore.entity.CloudDo;
//import cn.infocore.entity.User;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.LinkedList;
//import java.util.List;
//
///**
// * @ProjectName: mailalarm
// * @Package: cn.infocore.manager
// * @ClassName: CloudClientManager
// * @Author: aren904
// * @Description:
// * @Date: 2021/5/13 11:05
// * @Version: 1.0
// */
//@Service
//public class CloudClientManager extends ServiceImpl< CloudMapper,CloudDo> {
//
//    @Autowired
//    CloudClientManager cloudClientManager;
//
//    @Autowired
//    UserManager userManager;
//
//
//    public void updateCloudClient(String uuid,CloudDo cloudDo){
//        LambdaQueryWrapper<CloudDo> cloudDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        LambdaQueryWrapper<CloudDo> queryWrapper = cloudDoLambdaQueryWrapper.eq(CloudDo::getUuId, uuid);
//        this.baseMapper.update(cloudDo,queryWrapper);
//    }
//
//
//    public List<String> getUserIdByUuid(String uuid){
//        LambdaQueryWrapper<CloudDo> cloudDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        //根据数据方舟uuid获取user_uuid,然后要到user表中根据user.id 找到user_uuid
//        cloudDoLambdaQueryWrapper.eq(CloudDo::getUuId,uuid);
//        List<CloudDo> CloudDoList = this.list(cloudDoLambdaQueryWrapper);
//        LinkedList<String> UserUuId = new LinkedList<>();
//        if(CloudDoList!=null){
//            for (CloudDo cloudDo:CloudDoList){
//                String userId = cloudDo.getUserId();
//                String userUuid= userManager.getUserIdById(userId);
//                UserUuId.add(userUuid);
//            }
//        }
//        return  UserUuId;
//    }
//
//}
