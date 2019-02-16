package com.dscjss.codingplatform.contests.dto;

import org.springframework.data.domain.Page;


public class Leaderboard {

    Page<Row> page;

    public Page<Row> getPage() {
        return page;
    }

    public void setPage(Page<Row> page) {
        this.page = page;
    }
}
