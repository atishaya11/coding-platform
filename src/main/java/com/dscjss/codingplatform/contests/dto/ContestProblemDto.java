package com.dscjss.codingplatform.contests.dto;

import com.dscjss.codingplatform.problems.dto.ProblemDto;


public class ContestProblemDto {

    private int id;
    private ProblemDto problemDto;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ProblemDto getProblemDto() {
        return problemDto;
    }

    public void setProblemDto(ProblemDto problemDto) {
        this.problemDto = problemDto;
    }
}
