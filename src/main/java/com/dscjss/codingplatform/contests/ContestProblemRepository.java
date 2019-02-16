package com.dscjss.codingplatform.contests;

import com.dscjss.codingplatform.contests.dto.ContestProblemDto;
import com.dscjss.codingplatform.contests.model.ContestProblem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestProblemRepository extends JpaRepository<ContestProblem, Integer> {


    ContestProblem findByProblemCodeAndContestId(String code, int contestId);

    ContestProblem findByProblemIdAndContestId(int id, int contestId);
}
