package com.dscjss.codingplatform.contests.dto;

import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

public class ContestDto {

    private int id;
    private String name;
    private String code;

    @SafeHtml
    private String description;

    @SafeHtml
    private String prizes;

    @SafeHtml
    private String rules;

    @SafeHtml
    private String scoring;

    private List<ContestProblemDto> problemDtoList;

    @DateTimeFormat(pattern = "dd-MM-yyyy hh:mm")
    private Date startDate;

    @DateTimeFormat(pattern = "dd-MM-yyyy hh:mm")
    private Date endDate;

    private boolean registered;


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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrizes() {
        return prizes;
    }

    public void setPrizes(String prizes) {
        this.prizes = prizes;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getScoring() {
        return scoring;
    }

    public void setScoring(String scoring) {
        this.scoring = scoring;
    }

    public List<ContestProblemDto> getProblemDtoList() {
        return problemDtoList;
    }

    public void setProblemDtoList(List<ContestProblemDto> problemDtoList) {
        this.problemDtoList = problemDtoList;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }


    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }
}
