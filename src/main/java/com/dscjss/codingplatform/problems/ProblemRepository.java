package com.dscjss.codingplatform.problems;

import com.dscjss.codingplatform.problems.model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem, Integer>, PagingAndSortingRepository<Problem, Integer> {

    Problem findByCode(String code);

    Integer countByCodeIsStartingWith(String code);

    List<Problem> findByAuthor_Id(Integer id);
}
