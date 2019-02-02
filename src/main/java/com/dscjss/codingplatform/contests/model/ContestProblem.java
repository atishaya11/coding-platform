package com.dscjss.codingplatform.contests.model;

import com.dscjss.codingplatform.problems.model.Problem;

import javax.persistence.*;

@Entity
@Table(name = "contest_problems")
public class ContestProblem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;


    @ManyToOne
    @JoinColumn(name = "contest_id")
    private Contest contest;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public Contest getContest() {
        return contest;
    }

    public void setContest(Contest contest) {
        this.contest = contest;
    }
}
