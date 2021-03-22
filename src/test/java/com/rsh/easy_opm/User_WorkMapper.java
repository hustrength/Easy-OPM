package com.rsh.easy_opm;

import com.rsh.easy_opm.annotation.*;

import java.util.List;

@Mapper
@ResultMap(id = "workInfoMapper",
        idNode = @ResultsId(column = "id", property = "id"),
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
        idNode = @ResultsId(column = "id", property = "id"),
        result = {
                @Result(column = "nick_name", property = "nickName"),
                @Result(column = "real_name", property = "realName")},
        association = @Association(property = "workInfos", ofType = WorkInfo.class,
                select = "queryWorkById", column = "id",
                result = {
                        @Result(column = "work_id", property = "work_id"),
                        @Result(column = "user_id", property = "user_id"),
                        @Result(column = "company", property = "company"),
                        @Result(column = "position", property = "position"),
                        @Result(column = "start", property = "start"),
                        @Result(column = "departure", property = "departure")
                }))
@ResultMap(id = "ResultWork", result = {
        @Result(column = "work_id", property = "workId"),
        @Result(column = "user_id", property = "userId")})
public interface User_WorkMapper {
    /**
     * @param id
     * @return
     */
    @Select("select * from user, workinfo " +
            "where id=user_id and id=#{id}")
    @ParamType(Integer.class)
    @ResultType(User.class)
    @ResultMapId("workInfoMapper")
    User queryUserWorkByUserId(int id);

    /**
     * @param id
     * @return
     */
    @Select("select * from user " +
            "where id=#{id}")
    @ParamType(Integer.class)
    @ResultType(User.class)
    @ResultMapId("TwoStepUserMapper")
    User queryUserById(int id);

    /**
     * @param id
     * @return
     */
    @Select("select * from workinfo " +
            "where user_id=#{id}")
    @ParamType(Integer.class)
    @ResultType(WorkInfo.class)
    @ResultMapId("ResultWork")
    List<WorkInfo> queryWorkById(int id);
}
