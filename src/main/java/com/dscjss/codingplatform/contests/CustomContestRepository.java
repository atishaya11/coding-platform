package com.dscjss.codingplatform.contests;

public interface CustomContestRepository  {


    void updateLeaderboard(int contestId, int contestType);

    void updateCliSubmissionsScores(int contestId, int problemId);
}
