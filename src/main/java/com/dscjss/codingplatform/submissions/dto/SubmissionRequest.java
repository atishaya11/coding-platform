package com.dscjss.codingplatform.submissions.dto;

import com.dscjss.codingplatform.users.dto.UserBean;

public class SubmissionRequest {

    private UserBean userBean;
    private String source;
    private int compilerId;
    private int problemId;
    private boolean isPublic;
    private boolean visible;
    private int contestId;
    private boolean forContest;
    private int contestProblemId; // Contest problems are separately stored but submissions are mapped only by actual problem. See ContestProblem.java for more details
    private int maxScore;
    private int judgeId;

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getCompilerId() {
        return compilerId;
    }

    public void setCompilerId(int compilerId) {
        this.compilerId = compilerId;
    }

    public int getProblemId() {
        return problemId;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
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

    public int getContestId() {
        return contestId;
    }

    public void setContestId(int contestId) {
        this.contestId = contestId;
    }

    public boolean isForContest() {
        return forContest;
    }

    public void setForContest(boolean forContest) {
        this.forContest = forContest;
    }

    public int getContestProblemId() {
        return contestProblemId;
    }

    public void setContestProblemId(int contestProblemId) {
        this.contestProblemId = contestProblemId;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public int getJudgeId() {
        return judgeId;
    }

    public void setJudgeId(int judgeId) {
        this.judgeId = judgeId;
    }
}
