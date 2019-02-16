package com.dscjss.codingplatform.contests.dto;

import com.dscjss.codingplatform.users.dto.UserBean;

public class Row {

    private UserBean user;
    private double score;

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
