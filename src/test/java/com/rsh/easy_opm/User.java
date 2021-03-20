package com.rsh.easy_opm;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class User implements Serializable {
    private int id;

    private String nickName;

    private String realName;

    private boolean sex;

    private int age;

    private WorkInfo workInfo;

    public User() {
    }

    public WorkInfo getWorkInfo() {
        return workInfo;
    }

    public void setWorkInfo(WorkInfo workInfo) {
        this.workInfo = workInfo;
    }

/*        public List<WorkInfo> getWorkInfos() {
        return workInfos;
    }

    public void setWorkInfos(List<WorkInfo> workInfos) {
        this.workInfos = workInfos;
    }*/

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

    public boolean isSex() {
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
//                ",\n\tWorkInfos={" + printWorkInfo() + '}' +
                ", workInfo='" + workInfo.toString() + '\'' +
                '}';
    }

    /*private String printWorkInfo() {
        if (workInfos == null)
            return null;
        StringBuilder result = new StringBuilder("");
        for (WorkInfo workInfo :
                workInfos) {
            result.append(workInfo.toString()).append(", ");
        }
        return result.substring(0, result.length() - 2);
    }*/
}
