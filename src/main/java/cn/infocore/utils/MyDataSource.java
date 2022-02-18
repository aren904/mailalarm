package cn.infocore.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;

//import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;

public class MyDataSource {
	
    public static DataSource dataSource;

    static {
        try {
            dataSource = null;
            Properties properties = new Properties();
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader("/usr/local/mailalarm/druid.properties"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            properties.load(reader);
            dataSource = DruidDataSourceFactory.createDataSource(properties);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static QueryRunner getQueryRunner() {
        QueryRunner query = null;
        try {
            query = new QueryRunner(dataSource);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return query;
    }

}
