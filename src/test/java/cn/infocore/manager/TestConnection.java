package cn.infocore.manager;


import cn.infocore.dto.DataArkDTO;
import cn.infocore.handler.DataArkHandler;
import cn.infocore.utils.MyDataSource;
//import cn.infocore.utils.MyDataSource1;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.SQLException;

@SpringBootTest
public class TestConnection {
//@Autowired
//MyDataSource1 myDataSource1;
//QueryRunner queryRunner = myDataSource1.getQueryRunner();
//    @Test
//    public void testCon() throws SQLException {
////        DataSource dataSource = myDataSource1.dataSource();
////        QueryRunner queryRunner = MyDataSource.getQueryRunner();
////        DataSource dataSource = myDataSource1.dataSource();
//        System.out.println(myDataSource1);
//        QueryRunner queryRunner = myDataSource1.getQueryRunner();
//        Integer id = 1;
//        Object[] parameter = {id};
//        DataArkDTO dto = queryRunner.query("select * from data_ark where id = ?", new DataArkHandler(), parameter);
//        System.out.println(dto);
//
////        DataArkDTO query = MyDataSource.getQueryRunner().query("select * from data_ark where id = ?", new DataArkHandler(), paremater)
////        System.out.println(dataSource);
//    }


//    @Test
//    public void testCon1() throws Exception {
////        DataSource dataSource = myDataSource1.dataSource();
////        QueryRunner queryRunner = MyDataSource.getQueryRunner();
////        DataSource dataSource = myDataSource1.dataSource();
//        System.out.println(myDataSource1);
//        QueryRunner queryRunner = MyDataSource1.getQueryRunner();
//        Integer id = 1;
//        Object[] parameter = {id};
//        DataArkDTO dto = queryRunner.query("select * from data_ark where id = ?", new DataArkHandler(), parameter);
//        System.out.println(dto);

//        DataArkDTO query = MyDataSource.getQueryRunner().query("select * from data_ark where id = ?", new DataArkHandler(), paremater)
//        System.out.println(dataSource);
//}
}
