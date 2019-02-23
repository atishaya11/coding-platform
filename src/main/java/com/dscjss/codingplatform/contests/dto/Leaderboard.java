package com.dscjss.codingplatform.contests.dto;

import org.springframework.data.domain.Page;


public class Leaderboard {

    private Page<Row> page;
    private ContestDto contestDto;

    public Page<Row> getPage() {
        return page;
    }

    public void setPage(Page<Row> page) {
        this.page = page;
    }

    public ContestDto getContestDto() {
        return contestDto;
    }

    public void setContestDto(ContestDto contestDto) {
        this.contestDto = contestDto;
    }
}
