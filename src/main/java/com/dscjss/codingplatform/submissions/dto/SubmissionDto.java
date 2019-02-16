package com.dscjss.codingplatform.submissions.dto;

import com.dscjss.codingplatform.compilers.dto.CompilerDto;
import com.dscjss.codingplatform.contests.dto.ContestDto;
import com.dscjss.codingplatform.problems.dto.ProblemDto;
import com.dscjss.codingplatform.submissions.model.Result;
import com.dscjss.codingplatform.users.dto.UserBean;

import java.util.Date;
import java.util.List;

public class SubmissionDto {

    private int id;
    private ProblemDto problemDto;
    private String source;
    private Date date;
    private UserBean userBean;
    private Result result;
    private CompilerDto compilerDto;
    private boolean isPublic;
    private boolean visible;
    private List<TestCaseResult> testCaseResultList;
    private boolean forContest;
    private ContestDto contestDto;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public ProblemDto getProblemDto() {
        return problemDto;
    }

    public void setProblemDto(ProblemDto problemDto) {
        this.problemDto = problemDto;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public CompilerDto getCompilerDto() {
        return compilerDto;
    }

    public void setCompilerDto(CompilerDto compilerDto) {
        this.compilerDto = compilerDto;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public List<TestCaseResult> getTestCaseResultList() {
        return testCaseResultList;
    }

    public void setTestCaseResultList(List<TestCaseResult> testCaseResultList) {
        this.testCaseResultList = testCaseResultList;
    }

    public boolean isForContest() {
        return forContest;
    }

    public void setForContest(boolean forContest) {
        this.forContest = forContest;
    }

    public ContestDto getContestDto() {
        return contestDto;
    }

    public void setContestDto(ContestDto contestDto) {
        this.contestDto = contestDto;
    }
}
