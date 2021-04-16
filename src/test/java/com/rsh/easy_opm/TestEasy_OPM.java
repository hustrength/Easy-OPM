package com.rsh.easy_opm;

import com.rsh.easy_opm.sqlsession.DefaultSession;
import com.rsh.easy_opm.sqlsession.SessionFactory;
import com.rsh.easy_opm.json.JsonMapper;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class TestEasy_OPM {

    @Test
    public void test() {
//        rdMapping();
        otherRdTest();
    }

    private void otherRdTest(){
        SessionFactory factory = new SessionFactory();
        DefaultSession sqlSession = factory.getSession(SessionFactory.DB_TYPE.RD);
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
//        List<User> result = userMapper.selectAll();
//        System.out.println(result);
        System.out.println(userMapper.queryAgeById(7));
    }

    private void gdMapping(){
        SessionFactory factory = new SessionFactory();

        DefaultSession cqlSession = factory.getSession(SessionFactory.DB_TYPE.GD);
        PersonMapper personMapper = cqlSession.getMapper(PersonMapper.class);

        Person result = personMapper.queryPersonByName("John");
//        result.printInfo();
        Person person = jsonMappingTest(result, "output/person_output.txt");
        person.printInfo();
    }

    private void rdMapping(){
        SessionFactory factory = new SessionFactory();

        DefaultSession sqlSession = factory.getSession(SessionFactory.DB_TYPE.RD);

        User_WorkMapper user_workMapper = sqlSession.getMapper(User_WorkMapper.class);

//        AssertError.setWarningOn(false);

        User result = user_workMapper.queryUserById(4);
        if (result != null)
            System.out.println(result.toString());

        User user= jsonMappingTest(result, "output/user_output.txt");
        System.out.println(user);
    }

    @SuppressWarnings("unchecked")
    private <T> T jsonMappingTest(T result, String path){
        JsonMapper jsonMapper = new JsonMapper();
        JsonMapper.setIndentOn(true);
//        JsonMapper.setOverwriteOn(true);

        System.out.println("\nMy POJ to json mapper:");
        System.out.println(jsonMapper.writeValueAsString(result));

        jsonMapper.writeValueAsFile(new File(path), result);

        T jsonResult = (T) jsonMapper.readValueFromFile(new File(path), result.getClass());
        System.out.println("\nMy json to POJ mapper:");
        return jsonResult;
    }
}

