package com.rsh.easy_opm;

import com.rsh.easy_opm.annotation.*;

import java.util.List;

@ResultMap(id = "PersonMapper",
        idNode = @Result(column = "$id", property = "id"),
        result = {
                @Result(column = "x", property = "this"),
                @Result(column = "@name", property = "name"),
                @Result(column = "@age", property = "age")
        },
        collection = @Collection(property = "friends", ofType = Friendship.class,
                select = "queryFriendshipByName", column = "name"),
        association = {
                @Association(property = "spouse", ofType = Person.class,
                        select = "querySpouseByName", column = "name"),
                @Association(property = "bornInfo", ofType = BornInfo.class,
                        select = "queryBornInfoByName", column = "name")})
@ResultMap(id = "SpouseInfoMapper",
        idNode = @Result(column = "$id", property = "id"),
        result = {
                @Result(column = "x", property = "this"),
                @Result(column = "@name", property = "name"),
                @Result(column = "@age", property = "age")
        })
@ResultMap(id = "BornInfoMapper",
        idNode = @Result(column = "$id", property = "id"),
        result = {
                @Result(column = "x", property = "this"),
                @Result(column = "@name", property = "name"),
                @Result(column = "y.year", property = "bornYear")
        },
        association = @Association(property = "place", ofType = Location.class,
                result = {
                        @Result(column = "z", property = "this"),
                        @Result(column = "$id", property = "id"),
                        @Result(column = "city", property = "city"),
                        @Result(column = "state", property = "state")}))
@ResultMap(id = "FriendshipInfo",
        idNode = @Result(column = "$id", property = "id"),
        result = {
                @Result(column = "x", property = "this"),
                @Result(column = "@since", property = "beginningYear")
        },
        association = @Association(property = "friend", ofType = Person.class,
                result = {
                        @Result(column = "y", property = "this"),
                        @Result(column = "name", property = "name"),
                        @Result(column = "age", property = "age"),
                        @Result(column = "$id", property = "id")
                }))
public interface PersonMapper {

    @Select("match (x:Person {name:#{name}}) return x;")
    @ParamType(String.class)
    @ResultType(Person.class)
    @ResultMapId("PersonMapper")
    Person queryPersonByName(String name);

    @Select("match (:Person {name:#{name}})-[:MARRIED]->(x) return x;")
    @ParamType(String.class)
    @ResultType(Person.class)
    @ResultMapId("SpouseInfoMapper")
    Person querySpouseByName(String name);

    @Select("match (x:Person {name:#{name}})-[y:BORN_IN]->(z) return x, y.year, z;")
    @ParamType(String.class)
    @ResultType(BornInfo.class)
    @ResultMapId("BornInfoMapper")
    BornInfo queryBornInfoByName(String name);

    @Select("match (:Person {name:#{name}})-[x:FRIENDS]->(y) return x, y;")
    @ParamType(String.class)
    @ResultType(Friendship.class)
    @ResultMapId("FriendshipInfo")
    List<Friendship> queryFriendshipByName(String name);
}
