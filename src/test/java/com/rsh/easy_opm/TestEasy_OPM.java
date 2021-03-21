package com.rsh.easy_opm;

import com.rsh.easy_opm.sqlsession.SqlSession;
import com.rsh.easy_opm.sqlsession.SqlSessionFactory;
import org.junit.Test;

import javax.print.DocFlavor;
import java.io.Serializable;
import java.util.*;

public class TestEasy_OPM {

    @Test
    public void test() {
        SqlSessionFactory factory = new SqlSessionFactory();
        SqlSession sqlSession = factory.getSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

        List<User> userList;

//        System.out.println("UserMapper.deleteByPrimaryKey(4):");
//        userMapper.deleteByPrimaryKey(4);
//        System.out.println("OK");
//        System.out.println();

//        userList = userMapper.selectAll();
//        System.out.println("UserMapper.selecetAll():");
//        for (User user :
//                userList) {
//            System.out.println(user.toString());
//        }
//        System.out.println();

//        String s = UserMapper.class.getName();
//        System.out.println("UserMapper.insertOne():");
//        User user = new User();
//        user.setId(4);
//        user.setNickName("Joey");
//        user.setRealName("Joey");
//        user.setSex(true);
//        user.setAge(21);
//        userMapper.insertOne(user);
//        System.out.println(user.toString());
//        System.out.println();

//        System.out.println("UserMapper.selecetByPrimaryKey(4):");
//        System.out.println(userMapper.selectByPrimaryKey(4));
//        System.out.println();

        System.out.println("UserMapper.selecetByConditions():");
        Map<String, Object> map = new HashMap<>();
        map.put("column1", "id");
        map.put("value1", 2);
        map.put("column2", "age");
        map.put("value2", 20);
        map.put("column3", "sex");
        map.put("value3", true);
        userList = userMapper.selectByConditions(map);
        for (User user1 :
                userList) {
            System.out.println(user1.toString());
        }
        System.out.println();


//        System.out.println("UserMapper.updateOne():");
//        User user = new User();
//        user.setId(4);
//        user.setNickName("Joey");
//        user.setRealName("Joey");
//        user.setSex(true);
//        user.setAge(21);
//        userMapper.updateOne(user);
//        System.out.println("OK");

//        User_WorkMapper user_workMapper = sqlSession.getMapper(User_WorkMapper.class);
//        List<User> results = user_workMapper.queryUserWorkByUserId(4);
//        for (User result :
//                results) {
//            System.out.println(result.toString());
//        }
    }
}

