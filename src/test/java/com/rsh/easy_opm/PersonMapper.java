package com.rsh.easy_opm;

public interface PersonMapper {
    Person queryPersonByName(String name);

    Person querySpouseByName(String name);

    BornInfo queryBornInfoByName(String name);

    Location queryBornLocationByName(String name);

    Friendship queryFriendshipByName(String name);
}
