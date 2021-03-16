package com.rsh.easy_opm;

import java.util.List;
import java.util.Map;

public interface UserMapper {
    User selectByPrimaryKey(int id);

    List<User> selectAll();

    void insertOne(User user);

    void deleteByPrimaryKey(int id);

    void updateOne(User user);

    List<User> selectByConditions(Map<String, Object> map);
}
