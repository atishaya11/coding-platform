package com.dscjss.codingplatform.submissions.model;

import com.dscjss.codingplatform.compilers.model.Compiler;
import com.dscjss.codingplatform.contests.model.Contest;
import com.dscjss.codingplatform.problems.model.Problem;
import com.dscjss.codingplatform.users.model.User;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "submission")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "remote_id")
    private Integer remoteId;

    @ManyToOne(targetEntity = Problem.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @ManyToOne(targetEntity = Compiler.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "compiler_id")
    private Compiler compiler;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    private Result result;

    @CreatedDate
    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    private boolean isPublic; // Solution will not be visible to the public

    private boolean visible; // Will not be shown on the submissions page.

    @Column(name = "for_contest")
    private boolean forContest;

    @ManyToOne
    @JoinColumn(name = "contest_id")
    private Contest contest;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(Integer remoteId) {
        this.remoteId = remoteId;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public Compiler getCompiler() {
        return compiler;
    }

    public void setCompiler(Compiler compiler) {
        this.compiler = compiler;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
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

    public boolean isForContest() {
        return forContest;
    }

    public void setForContest(boolean forContest) {
        this.forContest = forContest;
    }

    public Contest getContest() {
        return contest;
    }

    public void setContest(Contest contest) {
        this.contest = contest;
    }
}
