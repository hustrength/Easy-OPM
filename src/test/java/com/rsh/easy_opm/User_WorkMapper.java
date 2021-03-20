package com.rsh.easy_opm;

import java.util.List;

public interface User_WorkMapper {
    List<User> queryUserWorkByUserId(int id);

    User queryUserById(int id);

    User queryWorkById(int id);
}
