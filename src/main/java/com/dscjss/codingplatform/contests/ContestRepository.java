package com.dscjss.codingplatform.contests;

import com.dscjss.codingplatform.contests.model.Contest;
import com.dscjss.codingplatform.contests.model.ContestProblem;
import com.dscjss.codingplatform.problems.model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContestRepository extends JpaRepository<Contest, Integer>, CustomContestRepository {

    Integer countByCodeIsStartingWith(String tempCode);

    List<Contest> findByCreatedById(Integer id);

    Contest findByCode(String code);

}
