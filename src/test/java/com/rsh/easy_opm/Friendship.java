package com.rsh.easy_opm;

public class Friendship {
    private int id;
    private Person friend;
    private int beginningYear;

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

    public int getBeginningYear() {
        return beginningYear;
    }

    public void setBeginningYear(int beginningYear) {
        this.beginningYear = beginningYear;
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "id=" + id +
                ", friend=" + friend +
                ", beginningYear=" + beginningYear +
                '}';
    }
}
