package cn.infocore.Test;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

public class Test {

    /**
     * 通过配置文件名读取内容
     * @param fileName
     * @return
     */
    public static Properties readPropertiesFile(String fileName) {
        try {
            Resource resource = new ClassPathResource(fileName);
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            return props;
        } catch (Exception e) {
            System.out.println("————读取配置文件：" + fileName + "出现异常，读取失败————");
            e.printStackTrace();
        }
        return null;
    }




    public static void main(String[] args) throws Exception {
        Properties properties = readPropertiesFile("application.properties");
//        System.out.println(properties.getProperty("spring.datasource.username"));
//        System.out.println(properties.getProperty("spring.datasource.username"));
//        System.out.println(properties.getProperty("spring.datasource.username"));
//        System.out.println(properties.getProperty("spring.datasource.username"));
//        System.out.println(properties.getProperty("spring.datasource.username"));



        InputStream in = Test.class.getClassLoader().getResourceAsStream("druid.properties");
        Properties properties1 = new Properties();
        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

// 通过druid工厂，创建druid连接池对象
        DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);

// 从连接池获取连接
        for (int i = 1; i <= 11; i++) {
            Connection connection = dataSource.getConnection();
            // 进行jdbc的 crud操作
            System.out.println(connection);
            // 模拟归还
            if(i== 10){
                connection.close();
            }
            // 连接池使用详情
            System.out.println(dataSource);
        }
    }
}