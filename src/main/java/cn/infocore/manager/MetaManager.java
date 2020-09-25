package cn.infocore.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.dao.EcsMapper;
import cn.infocore.dao.MdbMapper;
import cn.infocore.entity.EcsDO;
import cn.infocore.entity.MdbDO;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Service
public class MetaManager extends ServiceImpl<MdbMapper, MdbDO> {

    public List<String> getMetaUserIdsById(String id) {
        LambdaQueryWrapper<MdbDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MdbDO::getId, id);
        LinkedList<String> userIdList = new LinkedList<>();

        Collection<MdbDO> mdbDOS = this.list(lambdaQueryWrapper);
        for (MdbDO mdbDO : mdbDOS) {
            userIdList.add(mdbDO.getUserId());
        }

        return userIdList;
    }
}
