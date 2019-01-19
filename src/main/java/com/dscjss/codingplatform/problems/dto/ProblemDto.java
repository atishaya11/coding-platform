package com.dscjss.codingplatform.problems.dto;

import com.dscjss.codingplatform.users.dto.UserBean;
import com.dscjss.codingplatform.users.model.User;
import com.dscjss.codingplatform.util.Status;
import org.hibernate.validator.constraints.SafeHtml;

import java.util.List;

public class ProblemDto {

    private Integer id;

    private String code;

    private String name;

    private boolean hasSolved; //If the current user has solved the problem

    private Status status;

    @SafeHtml
    private String description;

    @SafeHtml
    private String inputFormat;

    @SafeHtml
    private String outputFormat;

    @SafeHtml
    private String constraints;

    private List<Compiler> compilers;

    private List<TestCaseDto> testCaseDtoList;

    private UserBean author;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHasSolved() {
        return hasSolved;
    }

    public void setHasSolved(boolean hasSolved) {
        this.hasSolved = hasSolved;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInputFormat() {
        return inputFormat;
    }

    public void setInputFormat(String inputFormat) {
        this.inputFormat = inputFormat;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public String getConstraints() {
        return constraints;
    }

    public void setConstraints(String constraints) {
        this.constraints = constraints;
    }

    public List<Compiler> getCompilers() {
        return compilers;
    }

    public void setCompilers(List<Compiler> compilers) {
        this.compilers = compilers;
    }

    public List<TestCaseDto> getTestCaseDtoList() {
        return testCaseDtoList;
    }

    public void setTestCaseDtoList(List<TestCaseDto> testCaseDtoList) {
        this.testCaseDtoList = testCaseDtoList;
    }

    public UserBean getAuthor() {
        return author;
    }

    public void setAuthor(UserBean author) {
        this.author = author;
    }
}
