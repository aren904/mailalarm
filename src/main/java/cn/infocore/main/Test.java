package cn.infocore.main;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ColumnListHandler;

import cn.infocore.entity.MySnmp;
import cn.infocore.handler.ExceptHandler;
import cn.infocore.handler.ExecptHandler;
import cn.infocore.utils.MyDataSource;

public class Test {
	public static void main(String[] args) throws SQLException {
		/*System.out.println("----1----");
		Object[] para = {"1","2"};
		String sql="select * from test where clientId=? and streamerId=?";
		QueryRunner qr2 = MyDataSource.getQueryRunner();
		//db error
		System.out.println("start too query");
		List<Integer> dbErrors = qr2.query(sql, new ColumnListHandler<Integer>("exception"),para);
		System.out.println(dbErrors.size()+","+dbErrors.toString());*/
		
		//注意这里名称不一致，需要特殊处理
		/*List<String> currentErrors=new ArrayList<String>();
		String excepts="24";
		
		//current error
		if(excepts!=""&&excepts!=null){
			currentErrors.addAll(Arrays.asList(excepts.split(";")));
		}
		
		System.out.println("Current error size:"+currentErrors.size());
		for(String ex:currentErrors){
			System.out.println("Current error:"+ex);
		}*/
		
		MySnmp mySnmp=MySnmpCache.getInstance().getMySnmp();
		System.out.println(mySnmp.getStation_name());
		
		MySnmpCache.getInstance().updateMySnmp();
		mySnmp=MySnmpCache.getInstance().getMySnmp();
		System.out.println(mySnmp.getStation_name());
	}
}
