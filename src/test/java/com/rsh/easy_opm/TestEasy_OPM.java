package com.rsh.easy_opm;

import com.rsh.easy_opm.sqlsession.BasicSession;
import com.rsh.easy_opm.sqlsession.SessionFactory;
import com.rsh.easy_opm.json.JsonMapper;
import org.junit.Test;

import java.io.File;

public class TestEasy_OPM {

    @Test
    public void test() {
        SessionFactory factory = new SessionFactory();

        BasicSession sqlSession = factory.getSession(SessionFactory.DB_TYPE.RD);

        User_WorkMapper user_workMapper = sqlSession.getMapper(User_WorkMapper.class);

//        AssertError.setWarningOn(false);

        User result = user_workMapper.queryUserById(4);
        if (result != null)
            System.out.println(result.toString());

        JsonMapper jsonMapper = new JsonMapper();
        JsonMapper.setIndentOn(true);
        JsonMapper.setOverideOn(true);

        System.out.println("\nMy POJ to json mapper:");
        System.out.println(jsonMapper.writeValueAsString(result));

        jsonMapper.writeValueAsFile(new File("1.txt"), result);

        User jsonResult = jsonMapper.readValueFromFile(new File("1.txt"), User.class);
        System.out.println("\nMy json to POJ mapper:");
        System.out.println(jsonResult);

    }
}

