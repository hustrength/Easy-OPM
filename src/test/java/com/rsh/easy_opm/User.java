package com.rsh.easy_opm;

import java.io.Serializable;
import java.util.Set;

public class User implements Serializable {
    private int id;

    private String nickName;

    private String realName;

    private boolean sex;

    private int age;

    private Set<WorkInfo> workInfos;

    public User() {
    }

    public Set<WorkInfo> getWorkInfos() {
        return workInfos;
    }

    public void setWorkInfos(Set<WorkInfo> workInfos) {
        this.workInfos = workInfos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public boolean getSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickName='" + nickName + '\'' +
                ", realName='" + realName + '\'' +
                ", sex='" + sex + '\'' +
                ", age='" + age + '\'' +
                ",\n\tWorkInfos={" + printWorkInfo() + '}' +
                '}';
    }

    private String printWorkInfo() {
        if (workInfos == null)
            return null;
        StringBuilder result = new StringBuilder("");
        for (WorkInfo workInfo :
                workInfos) {
            result.append(workInfo.toString()).append(", ");
        }
        return result.substring(0, result.length() - 2);
    }
}
