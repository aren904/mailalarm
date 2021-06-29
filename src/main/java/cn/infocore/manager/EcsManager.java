package cn.infocore.manager;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.infocore.dao.EcsMapper;
import cn.infocore.entity.EcsDO;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Service
public class EcsManager extends ServiceImpl<EcsMapper, EcsDO>{
    
    public void updateByEcsId(String ecsId, EcsDO ecsDO) {
        LambdaQueryWrapper<EcsDO> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(EcsDO::getEcsId, ecsId);

        this.baseMapper.update(ecsDO, queryWrapper);
    }


    public List<String> getEcsUserIdsById(String id) {
        LambdaQueryWrapper<EcsDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EcsDO::getId, id);
        LinkedList<String> userIdList = new LinkedList<>();

        Collection<EcsDO> ecsDOS = this.list(lambdaQueryWrapper);
        for (EcsDO ecsDO : ecsDOS) {
            userIdList.add(ecsDO.getUserId());
        }

        return userIdList;
    }
}
