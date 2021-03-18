package com.rsh.easy_opm;

public interface User_WorkMapper {
    User queryUserWorkByUserId(int id);

    User queryUserById(int id);

    User queryWorkById(int id);
}
