package cn.infocore.dao;

import java.sql.SQLException;
import java.util.List;

import cn.infocore.entity.AlarmLogDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.log4j.Logger;

import cn.infocore.utils.MyDataSource;
import org.springframework.beans.factory.annotation.Autowired;

public class AlarmLogDAO {

	private static final Logger logger = Logger.getLogger(AlarmLogDAO.class);

	public static List<Integer> checkVmUncheckedException(String clientId) {

		String sql="select * from alarm_log where target_id=? and processed=0";
		Object[] condition=new Object[]{clientId};
		//db error
		QueryRunner qr = MyDataSource.getQueryRunner();
		List<Integer> dbErrors;
		try {
			dbErrors = qr.query(sql, new ColumnListHandler<Integer>("exception"),condition);
		} catch (SQLException e) {
			logger.warn(e);
			return null;
		}
		return dbErrors;
	}


}
