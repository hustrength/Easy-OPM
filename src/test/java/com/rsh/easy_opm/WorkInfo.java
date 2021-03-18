package com.rsh.easy_opm;

import java.util.Date;

public class WorkInfo {
    private int workId;
    private int userId;
    private String position;
    private Date start;
    private Date departure;
    private String company;

    public int getWorkId() {
        return workId;
    }

    public void setWorkId(int workId) {
        this.workId = workId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Date getDeparture() {
        return departure;
    }

    public void setDeparture(Date departure) {
        this.departure = departure;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @Override
    public String toString() {
        return "WorkInfo{" +
                "work_id=" + workId +
                ", user_id=" + userId +
                ", position='" + position + '\'' +
                ", start=" + start +
                ", departure=" + departure +
                ", company='" + company + '\'' +
                '}';
    }
}
