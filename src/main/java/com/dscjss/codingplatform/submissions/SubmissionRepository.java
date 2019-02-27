package com.dscjss.codingplatform.submissions;

import com.dscjss.codingplatform.submissions.model.Submission;
import com.dscjss.codingplatform.util.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Integer>, PagingAndSortingRepository<Submission, Integer> {

    Page<Submission> findByProblemCodeAndForContestIsFalseAndVisibleIsTrue(String code, Pageable pageable);

    Page<Submission> findByContestCodeAndProblemCodeAndVisibleIsTrue(String contest, String problem, Pageable pageable);

    default Page<Submission> findPracticeSubmissions(String code, Pageable pageable){
        return findByProblemCodeAndForContestIsFalseAndVisibleIsTrue(code, pageable);
    }
    default Page<Submission> findContestSubmissions(String contest, String problem, Pageable pageable){
        return findByContestCodeAndProblemCodeAndVisibleIsTrue(contest, problem, pageable);
    }

    Page<Submission> findByContestCode(String code, Pageable pageable);

    int countByProblemIdAndVisibleIsTrueAndForContestIsFalseAndResultStatus(int problemId, Status status);

    //TODO Custom query for group submissions by user while counting

    int countByProblemIdAndContestIdAndVisibleIsTrueAndForContestIsTrueAndResultStatus(int problemId, int contestId, Status status);

    default int countPracticeSubmissions(int problemId){
        return countByProblemIdAndVisibleIsTrueAndForContestIsFalseAndResultStatus(problemId, Status.CORRECT);
    }


    default int countContestSubmissions(int contestId, int problemId){
        return countByProblemIdAndContestIdAndVisibleIsTrueAndForContestIsTrueAndResultStatus(problemId, contestId, Status.CORRECT);
    }

    Page<Submission> findByProblemCodeAndUserIdAndForContestIsFalseAndVisibleIsTrue(String code, int userId, Pageable pageable);

    default Page<Submission> findPracticeSubmissionsForUser(String code, Pageable pageable, Integer id){
        return findByProblemCodeAndUserIdAndForContestIsFalseAndVisibleIsTrue(code, id, pageable);
    }

    Page<Submission> findByContestCodeAndUserIdAndProblemCodeAndVisibleIsTrue(String contest, int userId, String problem, Pageable pageable);

    default Page<Submission> findContestSubmissionsForUser(String contest, String problem, int id, Pageable pageable){
        return findByContestCodeAndUserIdAndProblemCodeAndVisibleIsTrue(contest, id, problem, pageable);
    }
}
