package com.dscjss.codingplatform.contests.model;

import com.dscjss.codingplatform.problems.model.AllowedCompiler;
import com.dscjss.codingplatform.users.model.User;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "contest")
public class Contest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String code;

    @Embedded
    private ContestBody body;

    @ManyToOne(targetEntity = User.class)
    private User owner;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "contest", targetEntity = ContestProblem.class)
    private List<ContestProblem> contestProblems;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "contest", targetEntity = RegisteredUser.class)
    private List<RegisteredUser> registeredUsers;


    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "creation_date")
    private Date creationDate;

    @Column(name = "last_modified_date")
    private Date lastModifiedDate;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public ContestBody getBody() {
        return body;
    }

    public void setBody(ContestBody body) {
        this.body = body;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<ContestProblem> getContestProblems() {
        return contestProblems;
    }

    public void setContestProblems(List<ContestProblem> contestProblems) {
        this.contestProblems = contestProblems;
    }

    public List<RegisteredUser> getRegisteredUsers() {
        return registeredUsers;
    }

    public void setRegisteredUsers(List<RegisteredUser> registeredUsers) {
        this.registeredUsers = registeredUsers;
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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
