package com.dscjss.codingplatform.submissions.model;

import com.dscjss.codingplatform.compilers.model.Compiler;
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

    @OneToOne
    private Result result;

    @CreatedDate
    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

}
