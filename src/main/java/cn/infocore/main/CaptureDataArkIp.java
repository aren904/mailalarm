package cn.infocore.main;

import StmStreamerDrManage.StreamerClouddrmanage;
import cn.infocore.entity.DataArkDO;
import cn.infocore.manager.DataArkManager;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CaptureDataArkIp {

    private static final Logger logger = Logger.getLogger(CaptureDataArkIp.class);
    @Autowired
    DataArkManager dataArkManager;


    public List<String> GetDataArkIp() {
        //dataArk.is_zombie=1为已经被强制删除的数据方舟，此处需要过滤掉被强制删除的数据方舟心跳
//        LambdaQueryWrapper<DataArkDO> dataArkDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        dataArkDOLambdaQueryWrapper.eq(DataArkDO::getIsZombie,0);
        StreamerClouddrmanage.SendDataArkIp.Builder builder = StreamerClouddrmanage.SendDataArkIp.newBuilder();

        List<DataArkDO> list = dataArkManager.list(null);
        StringBuilder sb = new StringBuilder();
        ArrayList<String> ips = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            for (DataArkDO dataArkDO : list) {
//                logger.info("数据方舟ip:"+dataArkDO.getIp());
//                sb.append(dataArkDO.getIp());
//                sb.append(";");
//
                ips.add(dataArkDO.getIp());
//                ips.add(";");
            }
 //           sb.deleteCharAt(sb.length() - 1);
//            return sb;
//            ips.remove(ips.size() -1);
        }
//        return sb.toString();
        return ips;
//        return null;
    }


    public StringBuffer GetDataArkIp1() {
        //dataArk.is_zombie=1为已经被强制删除的数据方舟，此处需要过滤掉被强制删除的数据方舟心跳
//        LambdaQueryWrapper<DataArkDO> dataArkDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        dataArkDOLambdaQueryWrapper.eq(DataArkDO::getIsZombie,0);
        List<DataArkDO> list = dataArkManager.list(null);
        StringBuffer sb = new StringBuffer();
        if (list != null && !list.isEmpty()) {
            for (DataArkDO dataArkDO : list) {
//                logger.info("数据方舟ip:"+dataArkDO.getIp());
                sb.append(dataArkDO.getIp());
                sb.append(";");
//
            }
            sb.deleteCharAt(sb.length() - 1);
//            return sb;
        }
        return sb;
//        return null;
    }


    public String getUuidByDataArkIp(String ip) {
        LambdaQueryWrapper<DataArkDO> dataArkDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dataArkDOLambdaQueryWrapper.eq(DataArkDO::getIp, ip);
        List<DataArkDO> DataList = dataArkManager.list(dataArkDOLambdaQueryWrapper);
        String uuid = null;
        if (DataList != null && DataList.size() > 0) {
            for (DataArkDO dataArkDO : DataList) {
                uuid = dataArkDO.getUuid();
            }
        }
        return uuid;
    }
}
