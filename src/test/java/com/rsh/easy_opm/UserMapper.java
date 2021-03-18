package com.rsh.easy_opm;

import com.rsh.easy_opm.annotation.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    /**
     * 1. Select a User by primary key
     * @param id primary key
     * @return User
     */
    @Select("select * from user\n" +
            "where id = #{id};")
    @ParamType(Integer.class)
//    @ResultMap("ResultUser")
    @ResultType(User.class)
    User selectByPrimaryKey(int id);

    /**
     * 2. Select all Users
     * @return List<User>
     */
    @Select("select * from user")
    @Results(id="ResultUser", value = {
            @Result(column = "nick_name", property = "nickName"),
            @Result(column = "real_name", property = "realName")
    })
    @ResultType(User.class)
    List<User> selectAll();

    /**
     * 3. Insert User
     * @param user User Class
     */
    @Insert("insert into user\n" +
            "values (#{id}, #{nickName}, #{realName}, #{sex}, #{age});")
    @ParamType(User.class)
    @ResultMap("ResultUser")
    void insertOne(User user);

    /**
     * 4. Delete a User by primary key
     * @param id primary key
     */
    @Delete("delete from user\n" +
            "where id=#{id};")
    @ParamType(Integer.class)
    void deleteByPrimaryKey(int id);

    /**
     * 5. Update a User
     * @param user User Class
     */
    @Update("update user\n" +
            "set nick_name=#{nickName}, age=#{age}\n" +
            "where id=#{id}")
    @ParamType(User.class)
    void updateOne(User user);

    /**
     * 6. Select Users by some conditions
     * @param map Map of conditions
     * @return List<User>
     */
    @Select("select\n" +
            "real_name,\n" +
            "age\n" +
            "from user\n" +
            "where ${column1} > #{value1} and ${column2} > #{value2} and ${column3} = #{value3};")
    @ParamType(Map.class)
    @ResultType(User.class)
    @ResultMap("ResultUser")
    List<User> selectByConditions(Map<String, Object> map);
}
