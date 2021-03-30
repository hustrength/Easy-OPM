package com.rsh.easy_opm;

import java.util.List;

public interface PersonMapper {
    Person queryPersonByName(String name);

    Person querySpouseByName(String name);

    BornInfo queryBornInfoByName(String name);

    Location queryBornLocationByName(String name);

    List<Friendship> queryFriendshipByName(String name);
}
