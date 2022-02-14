//package cn.infocore.utils;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.Properties;
//
//import javax.sql.DataSource;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import com.alibaba.druid.pool.DruidDataSourceFactory;
//import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
//import org.apache.commons.dbutils.QueryRunner;
//import org.apache.log4j.Logger;
//import com.mchange.v2.c3p0.ComboPooledDataSource;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class MyDataSource1 {
//    private static final Logger logger = Logger.getLogger(MyDataSource.class.getName());
//    // 通过标识名来创建相应连接池
////    private static DataSource  dataSource = new ComboPooledDataSource("mysql");
//
////    @Bean
////    @ConfigurationProperties(prefix = "spring.datasource")
////    public  DataSource dataSource(){
////        return new DruidDataSource();
////    }
//
//
//    public  static QueryRunner getQueryRunner() throws Exception {
//        QueryRunner query=new QueryRunner(dataSource1());
//        return query;
//    }
//    public static DataSource dataSource1() throws Exception {
//        Properties properties = new Properties();
//        BufferedReader reader = new BufferedReader(new FileReader("D:\\桌面\\新建文件夹 (5)\\druid.properties"));
//        try {
//            properties.load(reader);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println(properties);
//        DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);
//        return dataSource;
//    }
//
//    public static void main(String[] args) throws Exception {
//        DataSource dataSource = dataSource1();
//        System.out.println(dataSource);
//    }
//
//
//}
