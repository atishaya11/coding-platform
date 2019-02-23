package com.dscjss.codingplatform.problems.dto;

import com.dscjss.codingplatform.compilers.dto.CompilerDto;
import com.dscjss.codingplatform.submissions.model.Result;
import com.dscjss.codingplatform.users.dto.UserBean;
import com.dscjss.codingplatform.util.Status;
import org.hibernate.validator.constraints.SafeHtml;

import java.util.List;

public class ProblemDto {

    private Integer id;

    private String code;

    private String name;

    private boolean hasSolved;

    private Result result;

    @SafeHtml
    private String description;

    @SafeHtml
    private String inputFormat;

    @SafeHtml
    private String outputFormat;

    @SafeHtml
    private String constraints;

    @SafeHtml
    private String explanation;

    private List<CompilerDto> compilers;

    private List<TestCaseDto> testCaseDtoList;

    private UserBean author;

    private int minTimeLimit;

    private int maxTimeLimit;

    private boolean practice;

    private int submissionCount;

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

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
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

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public List<CompilerDto> getCompilers() {
        return compilers;
    }

    public void setCompilers(List<CompilerDto> compilers) {
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

    public int getMinTimeLimit() {
        return minTimeLimit;
    }

    public void setMinTimeLimit(int minTimeLimit) {
        this.minTimeLimit = minTimeLimit;
    }

    public int getMaxTimeLimit() {
        return maxTimeLimit;
    }

    public void setMaxTimeLimit(int maxTimeLimit) {
        this.maxTimeLimit = maxTimeLimit;
    }

    public boolean isPractice() {
        return practice;
    }

    public void setPractice(boolean practice) {
        this.practice = practice;
    }

    public int getSubmissionCount() {
        return submissionCount;
    }

    public void setSubmissionCount(int submissionCount) {
        this.submissionCount = submissionCount;
    }
}
