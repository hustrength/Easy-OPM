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
public interface User_WorkMapper {
    @Select("select * from user, workinfo " +
            "where id=user_id and id=#{id}")
    @ParamType(Integer.class)
    @ResultType(User.class)
    @ResultMapId("workInfoMapper")
    User queryUserWorkByUserId(int id);

    User queryUserById(int id);

    List<WorkInfo> queryWorkById(int id);
}
