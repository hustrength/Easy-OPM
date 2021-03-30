package com.rsh.easy_opm;

public class BornInfo {
    private int id;
    private String name;
    private Location place;
    private int bornYear;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getPlace() {
        return place;
    }

    public void setPlace(Location place) {
        this.place = place;
    }

    public int getBornYear() {
        return bornYear;
    }

    public void setBornYear(int bornYear) {
        this.bornYear = bornYear;
    }

    @Override
    public String toString() {
        return "BornInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", place=" + place.toString() +
                ", bornYear=" + bornYear +
                '}';
    }
}
