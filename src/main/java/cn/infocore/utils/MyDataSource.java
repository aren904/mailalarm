package cn.infocore.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

//import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;
//import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


public class MyDataSource {
    private static final Logger logger = Logger.getLogger(MyDataSource.class.getName());
    // 通过标识名来创建相应连接池
//	private static DataSource  dataSource = new ComboPooledDataSource("mysql");
//
//	// 从连接池中取用一个连接
//	public static Connection getConnection() {
//		Connection connection = null;
//		try {
//			connection = dataSource.getConnection();
//
//		} catch (Exception e) {
//			logger.error("Exception happened when get database connection.", e);
//		}
//		return connection;
//	}
//
//	// 释放连接回连接池
//	public static void close(Connection conn) {
//
//		if (conn != null) {
//			try {
//				conn.close();
//			} catch (SQLException e) {
//				logger.error("Exception in happened when close database connection.", e);
//			}
//		}
//	}
//
//	//获取数据源
//	public static DataSource getDataSource() {
//		return dataSource;
//	}
//
//	public static QueryRunner getQueryRunner(){
//		QueryRunner query=new QueryRunner(dataSource);
//		return query;
//	}

    //	@Bean
//	@ConfigurationProperties(prefix = "spring.datasource")
//	public  DataSource dataSource() {
//		DruidDataSource druidDataSource = new DruidDataSource();
//		return druidDataSource;
//	}
//	public  QueryRunner getQueryRunner(){
//		QueryRunner query=new QueryRunner(dataSource());
//		return query;
//	}
    public static DataSource dataSource;

    static {
        try {
            dataSource = null;
            Properties properties = new Properties();
            //注意这边以后要改成druid所在的路径，基本不会变动，同时用mvn打包的时候需要将application.properties放到外面然后注释打包和jar放在一块
//		BufferedReader reader = new BufferedReader(new FileReader("/usr/local/mailalarm/druid.properties"));//生产环境配置
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader("/usr/local/mailalarm/druid.properties"));
//                reader = new BufferedReader(new FileReader("D:\\桌面\\新建文件夹 (7)\\druid.properties"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
//		BufferedReader reader = new BufferedReader(new FileReader("D:\\桌面\\TestMailalarm\\application.properties"));

            properties.load(reader);

            System.out.println(properties);


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
