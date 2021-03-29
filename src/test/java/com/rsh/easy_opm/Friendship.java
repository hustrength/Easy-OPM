package com.rsh.easy_opm;

public class Friendship {
    private int id;
    private Person friend;
    private String beginningYear;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Person getFriend() {
        return friend;
    }

    public void setFriend(Person friend) {
        this.friend = friend;
    }

    public String getBeginningYear() {
        return beginningYear;
    }

    public void setBeginningYear(String beginningYear) {
        this.beginningYear = beginningYear;
    }
}
