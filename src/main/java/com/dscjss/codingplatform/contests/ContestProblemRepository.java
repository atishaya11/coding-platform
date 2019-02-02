package com.dscjss.codingplatform.contests;

import com.dscjss.codingplatform.contests.model.ContestProblem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestProblemRepository extends JpaRepository<ContestProblem, Integer> {
}
