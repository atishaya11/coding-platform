package com.dscjss.codingplatform.problems.model;


import com.dscjss.codingplatform.submissions.model.Submission;
import com.dscjss.codingplatform.users.model.User;
import org.hibernate.annotations.Formula;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "problem")
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    private String name;

    @Embedded
    private ProblemBody body;

    @Column(name = "judge_id")
    private int judgeId;

    @ManyToOne(targetEntity = User.class)
    private User author;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "problem", targetEntity = AllowedCompiler.class)
    private Set<AllowedCompiler> allowedCompilers;

    @OneToMany
    @OrderColumn(name = "test_case_idx")
    private List<TestCase> testCases;

    @OneToMany
    private Set<Submission> submissions;

    @CreatedDate
    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @LastModifiedDate
    @Column(name = "modification_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modificationDate;

    @Column(name = "is_practice")
    private boolean practice;

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

    public ProblemBody getBody() {
        return body;
    }

    public void setBody(ProblemBody body) {
        this.body = body;
    }

    public int getJudgeId() {
        return judgeId;
    }

    public void setJudgeId(int judgeId) {
        this.judgeId = judgeId;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Set<AllowedCompiler> getAllowedCompilers() {
        return allowedCompilers;
    }

    public void setAllowedCompilers(Set<AllowedCompiler> allowedCompilers) {
        this.allowedCompilers = allowedCompilers;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }

    public Set<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(Set<Submission> submissions) {
        this.submissions = submissions;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public boolean isPractice() {
        return practice;
    }

    public void setPractice(boolean practice) {
        this.practice = practice;
    }

}
