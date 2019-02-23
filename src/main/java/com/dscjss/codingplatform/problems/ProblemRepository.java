package com.dscjss.codingplatform.problems;

import com.dscjss.codingplatform.problems.model.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem, Integer>, PagingAndSortingRepository<Problem, Integer> {

    Problem findByCode(String code);

    Integer countByCodeIsStartingWith(String code);

    List<Problem> findByAuthor_Id(Integer id);

    Page<Problem> findAllByPracticeIsTrue(Pageable pageable);

    @Modifying
    @Query("UPDATE Problem p SET p.practice = ?1 WHERE p.id = ?2")
    void setPracticeById(boolean practice, int id);

}
