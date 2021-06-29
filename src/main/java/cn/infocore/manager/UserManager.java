package cn.infocore.manager;

import cn.infocore.dao.UserMapper;
import cn.infocore.entity.User;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: mailalarm
 * @Package: cn.infocore.manager
 * @ClassName: UserManager
 * @Author: aren904
 * @Description:
 * @Date: 2021/6/9 14:49
 * @Version: 1.0
 */
@Service
public class UserManager extends ServiceImpl<UserMapper, User> {

    public String getUserIdById(String userId) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getId, userId);
        User user = this.getOne(lambdaQueryWrapper);
        String uuid = user.getUuid();
        return uuid;
    }
}
