package com.dscjss.codingplatform.submissions.model;


import com.dscjss.codingplatform.util.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@Table(name = "submission_result")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(value = EnumType.ORDINAL)
    private Status status;
    private double score;
    private int time;
    private double memory;
    @Column(name = "source_char_count")
    private Integer sourceCharCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public double getMemory() {
        return memory;
    }

    public void setMemory(double memory) {
        this.memory = memory;
    }

    public Integer getSourceCharCount() {
        return sourceCharCount;
    }

    public void setSourceCharCount(Integer sourceCharCount) {
        this.sourceCharCount = sourceCharCount;
    }
}
