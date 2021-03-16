package com.rsh.easy_opm;

import com.rsh.easy_opm.sqlsession.SqlSession;
import com.rsh.easy_opm.sqlsession.SqlSessionFactory;
import org.junit.Test;

import javax.print.DocFlavor;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
//
//        userList = userMapper.selectAll();
//        System.out.println("UserMapper.selecetAll():");
//        for (User user :
//                userList) {
//            System.out.println(user.toString());
//        }
//        System.out.println();
//
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
//
//        System.out.println("UserMapper.selecetByPrimaryKey(4):");
//        System.out.println(userMapper.selectByPrimaryKey(4));
//        System.out.println();

        Map<String, Object> map = new HashMap<>();
        map.put("idMin", 1);
        map.put("gender", true);
        userList = userMapper.selectByConditions(map);
        System.out.println("UserMapper.selecetByConditions():");
        for (User user1 :
                userList) {
            System.out.println(user1.toString());
        }
        System.out.println();

        System.out.println("UserMapper.updateOne():");
        User user = new User();
        user.setId(4);
        user.setNickName("Joey");
        user.setRealName("Joey");
        user.setSex(true);
        user.setAge(21);
        userMapper.updateOne(user);
        System.out.println("OK");

    }
}

