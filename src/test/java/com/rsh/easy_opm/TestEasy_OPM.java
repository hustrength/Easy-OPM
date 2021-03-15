package com.rsh.easy_opm;

import com.rsh.easy_opm.sqlsession.SqlSession;
import com.rsh.easy_opm.sqlsession.SqlSessionFactory;
import org.junit.Test;

import java.io.Serializable;
import java.util.List;

public class TestEasy_OPM {

    @Test
    public void test() {
        SqlSessionFactory factory = new SqlSessionFactory();
        SqlSession sqlSession = factory.getSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        List<User> userList = userMapper.selectAll();
        System.out.println("userMapper.selecetAll():");
        for (User user :
                userList) {
            System.out.println(user.toString());
        }
        System.out.println();

        System.out.println("userMapper.selecetByPrimaryKey():");
        System.out.println(userMapper.selectByPrimaryKey(2));
    }
}

