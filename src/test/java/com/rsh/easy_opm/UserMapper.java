package com.rsh.easy_opm;

import java.util.List;

public interface UserMapper {
    User selectByPrimaryKey(int id);

    List<User> selectAll();

    void insertOne(User user);

    void deleteByPrimaryKey(int id);
}
