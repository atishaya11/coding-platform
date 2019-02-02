package com.dscjss.codingplatform.contests;

import com.dscjss.codingplatform.contests.dto.ContestDto;
import com.dscjss.codingplatform.contests.dto.ContestProblemDto;
import com.dscjss.codingplatform.users.dto.UserBean;

import java.util.List;

public interface ContestService {

    ContestDto getContestById(UserBean userBean, Integer id, boolean onlySummary);

    ContestDto getContestByCode(UserBean userBean, String code, boolean onlySummary);

    ContestDto createContestFromName(UserBean userBean, String name);

    List<ContestDto> getContestsByAuthor(UserBean userBean);

    void updateContestDetails(UserBean userBean, ContestDto contestDto, Integer id);

    boolean addProblem(UserBean userBean, Integer id, Integer problemId);

    List<ContestProblemDto> getContestProblems(UserBean userBean, int id);

    boolean removeProblem(UserBean userBean, Integer id, Integer contestProblemId);
}
