package com.dscjss.codingplatform.contests;

import com.dscjss.codingplatform.contests.dto.ContestProblemDto;
import com.dscjss.codingplatform.contests.model.ContestProblem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestProblemRepository extends JpaRepository<ContestProblem, Integer> {


    ContestProblem findByProblemCode(String code);

    ContestProblem findByProblemId(int id);
}
