package com.dscjss.codingplatform.submissions.dto;

import com.dscjss.codingplatform.users.dto.UserBean;

public class SubmissionRequest {

    private UserBean userBean;
    private String source;
    private int compilerId;
    private int problemId;
    private boolean isPublic;
    private boolean visible;

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
}
