package com.rsh.easy_opm;

import com.rsh.easy_opm.session.DefaultSession;
import com.rsh.easy_opm.session.SessionFactory;
import com.rsh.easy_opm.json.JsonMapper;
import org.junit.Test;

import java.io.File;

public class TestEasy_OPM {

    @Test
    public void test() {
        gdMapping();
//        otherRdTest();
    }

    private void otherRdTest() {
        SessionFactory factory = new SessionFactory();
        DefaultSession sqlSession = factory.getSession(SessionFactory.DB_TYPE.RD);
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
//        List<User> result = userMapper.selectAll();
//        System.out.println(result);
        System.out.println(userMapper.queryAgeById(7));
    }

    private void gdMapping() {
        SessionFactory factory = new SessionFactory();

        DefaultSession cqlSession = factory.getSession(SessionFactory.DB_TYPE.GD);
        PersonMapper personMapper = cqlSession.getMapper(PersonMapper.class);

        Person result = personMapper.queryPersonByName("John");

        System.out.println("\nQuery result:\n>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        result.printInfo();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>");

        Person person = jsonMappingTest(result, "output/person_output.txt");
        person.printInfo();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>");

    }

    private void rdMapping() {
        SessionFactory factory = new SessionFactory();

        DefaultSession sqlSession = factory.getSession(SessionFactory.DB_TYPE.RD);

        User_WorkMapper user_workMapper = sqlSession.getMapper(User_WorkMapper.class);

//        AssertError.setWarningOn(false);

//        User result = user_workMapper.queryUserById(4);
        User result = user_workMapper.queryUserWorkByUserId(4);

        if (result != null) {
            System.out.println("\nQuery result:\n>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println(result);
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }

        User user = jsonMappingTest(result, "output/user_output.txt");
        System.out.println(user);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }

    @SuppressWarnings("unchecked")
    private <T> T jsonMappingTest(T result, String path) {
        JsonMapper jsonMapper = new JsonMapper();
        JsonMapper.setIndentOn(true);
        JsonMapper.setOverwriteOn(true);

        System.out.println("\nMy POJO to Json mapper:\n>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(jsonMapper.writeValueAsString(result));
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>");

        jsonMapper.writeValueAsFile(new File(path), result);

        T jsonResult = (T) jsonMapper.readValueFromFile(new File(path), result.getClass());
        System.out.println("\nMy Json to POJO mapper:\n>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        return jsonResult;
    }
}

