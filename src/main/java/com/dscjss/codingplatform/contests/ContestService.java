package com.dscjss.codingplatform.contests;

import com.dscjss.codingplatform.contests.dto.*;
import com.dscjss.codingplatform.contests.exception.NotFoundException;
import com.dscjss.codingplatform.error.InvalidRequestException;
import com.dscjss.codingplatform.error.UserNotFoundException;
import com.dscjss.codingplatform.submissions.dto.SubmissionDto;
import com.dscjss.codingplatform.submissions.dto.SubmissionRequest;
import com.dscjss.codingplatform.submissions.exception.InvalidSubmissionException;
import com.dscjss.codingplatform.submissions.exception.SubmissionFailedException;
import com.dscjss.codingplatform.users.dto.UserBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ContestService {

    ContestDto getContestById(UserBean userBean, Integer id, boolean onlySummary);

    ContestDto getContestByCode(UserBean userBean, String code, boolean onlySummary);

    ContestDto createContestFromName(UserBean userBean, String name);

    List<ContestDto> getContests(UserBean userBean);

    List<ContestDto> getContestsByAuthor(UserBean userBean);

    void updateContestDetails(UserBean userBean, ContestDto contestDto, Integer id);

    boolean addProblem(UserBean userBean, Integer id, Integer problemId);

    List<ContestProblemDto> getContestProblems(UserBean userBean, int id);

    boolean removeProblem(UserBean userBean, Integer id, Integer contestProblemId);

    ContestProblemDto getProblem(UserBean userBean, String code, String problemCode);

    int submit(SubmissionRequest submissionRequest) throws InvalidSubmissionException, SubmissionFailedException;

    Leaderboard getLeaderboard(UserBean userBean, String code, Pageable pageable);

    void registerUser(UserBean userBean, String code) throws UserNotFoundException, NotFoundException;

    Page<SubmissionDto> getSubmissions(UserBean userBean, String code, String problem, Pageable pageable);

    void updateContestProblems(UserBean userBean, Integer id, ProblemsUpdateForm problemsUpdateForm) throws InvalidRequestException;

    void updateContestSettings(UserBean userBean, Integer id, Settings settings);
}
