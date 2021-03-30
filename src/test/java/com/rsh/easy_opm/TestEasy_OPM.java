package com.rsh.easy_opm;

import com.rsh.easy_opm.sqlsession.BasicSession;
import com.rsh.easy_opm.sqlsession.DefaultSession;
import com.rsh.easy_opm.sqlsession.SessionFactory;
import com.rsh.easy_opm.json.JsonMapper;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class TestEasy_OPM {

    @Test
    public void test() {
        gdMapping();

    }

    private void gdMapping(){
        SessionFactory factory = new SessionFactory();

        DefaultSession cqlSession = factory.getSession(SessionFactory.DB_TYPE.GD);
        PersonMapper personMapper = cqlSession.getMapper(PersonMapper.class);

        Person result = personMapper.queryPersonByName("John");

        if (result != null) {
            System.out.println("\nBasicInfo:");
            System.out.println(result);

            System.out.println("\nFriends:");
            List<Friendship> friends = result.getFriends();
            for (Friendship friend :
                    friends) {
                System.out.println(friend);
            }

            System.out.println("\nSpouse:");
            System.out.println(result.getSpouse());

            System.out.println("\nBornInfo:");
            System.out.println(result.getBornInfo());
        }
    }

    private void rdMapping(){
        SessionFactory factory = new SessionFactory();

        DefaultSession sqlSession = factory.getSession(SessionFactory.DB_TYPE.RD);

        User_WorkMapper user_workMapper = sqlSession.getMapper(User_WorkMapper.class);

//        AssertError.setWarningOn(false);

        User result = user_workMapper.queryUserById(4);
        if (result != null)
            System.out.println(result.toString());

        jsonMapping(result);
    }

    private void jsonMapping(Object result){
        JsonMapper jsonMapper = new JsonMapper();
        JsonMapper.setIndentOn(true);
//        JsonMapper.setOverideOn(true);

        System.out.println("\nMy POJ to json mapper:");
        System.out.println(jsonMapper.writeValueAsString(result));

        jsonMapper.writeValueAsFile(new File("output/Json_output.txt"), result);

        User jsonResult = jsonMapper.readValueFromFile(new File("output/Json_output.txt"), User.class);
        System.out.println("\nMy json to POJ mapper:");
        System.out.println(jsonResult);
    }
}

