package com.dscjss.codingplatform.submissions;

import com.dscjss.codingplatform.submissions.model.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Integer>, PagingAndSortingRepository<Submission, Integer> {

    Page<Submission> findByProblemCodeAndForContestIsFalse(String code, Pageable pageable);

    Page<Submission> findByContestCodeAndProblemCode(String contest, String problem, Pageable pageable);
}
