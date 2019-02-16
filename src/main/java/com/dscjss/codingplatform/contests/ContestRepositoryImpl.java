package com.dscjss.codingplatform.contests;

import com.dscjss.codingplatform.util.Constants;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;


@Repository
public class ContestRepositoryImpl implements CustomContestRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void updateLeaderboard(int contestId, int contestType) {
        String queryString = "CALL update_scores(:id)";
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("id", contestId);
        query.executeUpdate();
    }


    @Override
    @Transactional
    public void updateCliSubmissionsScores(int contestId, int problemId) {
        String queryString = "CALL update_cli_submissions_scores(:contestId, :problemId)";
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("contestId", contestId);
        query.setParameter("problemId", problemId);
        query.executeUpdate();
    }
}
