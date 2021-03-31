package com.rsh.easy_opm;

import com.rsh.easy_opm.annotation.*;

import java.util.List;

@ResultMap(id = "workInfoMapper",
        idNode = @Result(column = "id", property = "id"),
        result = {
                @Result(column = "nick_name", property = "nickName"),
                @Result(column = "real_name", property = "realName")},
        collection = @Collection(property = "workInfos", ofType = WorkInfo.class,
                result = {
                        @Result(column = "work_id", property = "work_id"),
                        @Result(column = "user_id", property = "user_id"),
                        @Result(column = "company", property = "company"),
                        @Result(column = "position", property = "position"),
                        @Result(column = "start", property = "start"),
                        @Result(column = "departure", property = "departure")
                }))
@ResultMap(id = "TwoStepUserMapper",
        idNode = @Result(column = "id", property = "id"),
        result = {
                @Result(column = "nick_name", property = "nickName"),
                @Result(column = "real_name", property = "realName")},
        collection = @Collection(property = "workInfos", ofType = WorkInfo.class,
                select = "queryWorkById", column = "id"))
@ResultMap(id = "ResultWork", result = {
        @Result(column = "work_id", property = "workId"),
        @Result(column = "user_id", property = "userId")})
public interface User_WorkMapper {
    /**
     * 1. Select a User with WorkInfo by primary key in one-step query
     *
     * @param id primary key of User
     * @return User
     */
    @Select("select * from user, workinfo " +
            "where id=user_id and id=#{id}")
    @ParamType(Integer.class)
    @ResultType(User.class)
    @ResultMapId("workInfoMapper")
    User queryUserWorkByUserId(int id);

    /**
     * 2. Select a User with WorkInfo by primary key in two-step query
     *
     * @param id primary key of User
     * @return
     */
    @Select("select * from user " +
            "where id=#{id}")
    @ParamType(Integer.class)
    @ResultType(User.class)
    @ResultMapId("TwoStepUserMapper")
    User queryUserById(int id);

    /**
     * 3. Select a WorkInfo by primary key of User in one-step query
     *
     * @param id primary key of User
     * @return List<WorkInfo>
     */
    @Select("select * from workinfo " +
            "where user_id=#{id}")
    @ParamType(Integer.class)
    @ResultType(WorkInfo.class)
    @ResultMapId("ResultWork")
    List<WorkInfo> queryWorkById(int id);
}
