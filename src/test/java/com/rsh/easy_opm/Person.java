package com.rsh.easy_opm;

import java.util.List;

public class Person {
    private int id;
    private String name;
    private int age;
    private List<Friendship> friends;
    private Person spouse;
    private BornInfo bornInfo;

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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<Friendship> getFriends() {
        return friends;
    }

    public void setFriends(List<Friendship> friends) {
        this.friends = friends;
    }

    public Person getSpouse() {
        return spouse;
    }

    public void setSpouse(Person spouse) {
        this.spouse = spouse;
    }

    public BornInfo getBornInfo() {
        return bornInfo;
    }

    public void setBornInfo(BornInfo bornInfo) {
        this.bornInfo = bornInfo;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    public void printInfo() {
        System.out.println("\nBasicInfo:");
        System.out.println(this);

        System.out.println("\nFriends:");
        List<Friendship> friends = this.getFriends();
        for (Friendship friend :
                friends) {
            System.out.println(friend);
        }

        System.out.println("\nSpouse:");
        System.out.println(this.getSpouse());

        System.out.println("\nBornInfo:");
        System.out.println(this.getBornInfo());
    }
}
