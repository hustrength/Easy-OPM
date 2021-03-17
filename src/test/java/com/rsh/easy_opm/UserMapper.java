package com.rsh.easy_opm;

import com.rsh.easy_opm.annotation.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    User selectByPrimaryKey(int id);

    @Select(sql = "select * from user", paramType = "basic")
    @Results(id="ResultUser", value = {
            @Result(column = "nick_name", property = "nickName"),
            @Result(column = "real_name", property = "realName")
    })
    @ResultType(User.class)
    List<User> selectAll();

    @Insert(sql = "insert into user values (#{id}, #{nickName}, #{realName}, #{sex}, #{age});")
    @ResultMap("ResultUser")
    void insertOne(User user);

    void deleteByPrimaryKey(int id);

    void updateOne(User user);

    List<User> selectByConditions(Map<String, Object> map);
}
