package com.dscjss.codingplatform.contests;

import com.dscjss.codingplatform.contests.dto.*;
import com.dscjss.codingplatform.contests.exception.NotFoundException;
import com.dscjss.codingplatform.contests.model.*;
import com.dscjss.codingplatform.error.InvalidRequestException;
import com.dscjss.codingplatform.error.UserNotFoundException;
import com.dscjss.codingplatform.problems.ProblemRepository;
import com.dscjss.codingplatform.problems.ProblemService;
import com.dscjss.codingplatform.problems.dto.ProblemDto;
import com.dscjss.codingplatform.problems.model.Problem;
import com.dscjss.codingplatform.submissions.SubmissionRepository;
import com.dscjss.codingplatform.submissions.SubmissionService;
import com.dscjss.codingplatform.submissions.dto.SubmissionDto;
import com.dscjss.codingplatform.submissions.dto.SubmissionRequest;
import com.dscjss.codingplatform.submissions.exception.InvalidSubmissionException;
import com.dscjss.codingplatform.submissions.exception.SubmissionFailedException;
import com.dscjss.codingplatform.submissions.model.Submission;
import com.dscjss.codingplatform.users.UserRepository;
import com.dscjss.codingplatform.users.dto.UserBean;
import com.dscjss.codingplatform.users.model.User;
import com.dscjss.codingplatform.util.Constants;
import com.dscjss.codingplatform.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContestServiceImpl implements ContestService {

    private ContestRepository contestRepository;
    private UserRepository userRepository;
    private ProblemRepository problemRepository;
    private ContestProblemRepository contestProblemRepository;
    private ProblemService problemService;
    private SubmissionService submissionService;
    private RegisteredUserRepository registeredUserRepository;
    private final SubmissionRepository submissionRepository;

    @Autowired
    public void setSubmissionService(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @Autowired
    public ContestServiceImpl(ContestRepository contestRepository, UserRepository userRepository, ProblemRepository problemRepository, ContestProblemRepository contestProblemRepository, ProblemService problemService, RegisteredUserRepository registeredUserRepository, SubmissionRepository submissionRepository) {
        this.contestRepository = contestRepository;
        this.userRepository = userRepository;
        this.problemRepository = problemRepository;
        this.contestProblemRepository = contestProblemRepository;
        this.problemService = problemService;
        this.registeredUserRepository = registeredUserRepository;
        this.submissionRepository = submissionRepository;
    }

    @Override
    public ContestDto getContestById(UserBean userBean, Integer id, boolean onlySummary) {
        Contest contest = contestRepository.getOne(id);
        if(contest == null)
            throw new NotFoundException("Contest not found.");
        return getContestByCode(userBean, contest.getCode(), onlySummary);
    }

    @Override
    public ContestDto getContestByCode(UserBean userBean, String code, boolean onlySummary) {
        Contest contest = contestRepository.findByCode(code);
        if(contest == null)
            throw new NotFoundException("Contest not found.");
        ContestDto contestDto = Mapper.getContestDto(contest);
        if(userBean != null) {
            RegisteredUser registeredUser = registeredUserRepository.findByContestIdAndUserUsername(contest.getId(), userBean.getUsername());
            contestDto.setRegistered(registeredUser != null);
            contestDto.setCurrentUserLeaderboardRow(Mapper.getLeaderboardRow(registeredUser));
        }
        if(onlySummary){
            return contestDto;
        }else{

        }
        return contestDto;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ContestDto createContestFromName(UserBean userBean, String name) {

        User user = userRepository.findByUsername(userBean.getUsername());
        String tempCode = createTempCode(name.trim());
        Contest contest = new Contest();
        contest.setName(name);
        contest.setCreatedBy(user);
        contest.setCreationDate(new Date());
        contest.setLastModifiedDate(new Date());
        contest.setContestType(Constants.CONTEST_TYPE_DEFAULT);
        Integer countSimilar = contestRepository.countByCodeIsStartingWith(tempCode);
        if(countSimilar != 0)
            tempCode += "-" + countSimilar + 1;
        contest.setCode(tempCode);
        contestRepository.save(contest);
        return Mapper.getContestDto(contest);
    }

    private String createTempCode(String name){
        String lowerCaseTitle = name.toLowerCase();
        lowerCaseTitle = lowerCaseTitle.replace("[^A-Za-z0-9 ]", "");
        return lowerCaseTitle.replace(' ', '-');
    }

    @Override
    public List<ContestDto> getContests(UserBean userBean) {
        List<Contest> contests = contestRepository.findAll();
        List<ContestDto> contestDtoList = new ArrayList<>();
        contests.forEach(contest -> {
            contestDtoList.add(Mapper.getContestDto(contest));
        });
        return contestDtoList;
    }

    @Override
    public List<ContestDto> getContestsByAuthor(UserBean userBean) {
        List<Contest> contests = contestRepository.findByCreatedById(userBean.getId());
        List<ContestDto> contestDtoList = new ArrayList<>();
        contests.forEach(contest -> {
            contestDtoList.add(Mapper.getContestDto(contest));
        });
        return contestDtoList;
    }

    //TODO end date should be after start date
    @Override
    @Transactional
    public void updateContestDetails(UserBean userBean, ContestDto contestDto, Integer id) {
        Contest contest = contestRepository.getOne(id);
        contest.setCode(contestDto.getCode());
        contest.setName(contestDto.getName());
        contest.setLastModifiedDate(new Date());
        contest.setStartDate(contestDto.getStartDate());
        contest.setEndDate(contestDto.getEndDate());
        ContestBody body = contest.getBody();

        if(body == null){
            contest.setBody(new ContestBody());
        }
        contest.getBody().setDescription(contestDto.getDescription());
        contest.getBody().setPrizes(contestDto.getPrizes());
        contest.getBody().setRules(contestDto.getRules());
        contest.getBody().setScoring(contestDto.getScoring());

        contestRepository.save(contest);

    }

    @Override
    public boolean addProblem(UserBean userBean, Integer id, Integer problemId) {
        Problem problem = problemRepository.findById(problemId).orElse(null);
        if(problem == null)
            return false;

        Contest contest = contestRepository.findById(id).orElse(null);
        if(contest == null)
            return false;

        ContestProblem contestProblem = new ContestProblem();
        contestProblem.setContest(contest);
        contestProblem.setProblem(problem);
        List<ContestProblem> contestProblems = contest.getContestProblems();

        boolean exists = false;
        if (contestProblems != null) {
            for(ContestProblem temp : contestProblems){
                if(temp.getProblem().getId().equals(problemId)){
                    exists = true;
                    break;
                }
            }
        }else{
            contest.setContestProblems(new ArrayList<>());
        }
        if(!exists)
            contest.getContestProblems().add(contestProblem);
        contestRepository.save(contest);
        return true;
    }

    @Override
    public List<ContestProblemDto> getContestProblems(UserBean userBean, int id) {
        Contest contest = contestRepository.getOne(id);
        List<ContestProblem> contestProblems = contest.getContestProblems();
        if(contestProblems == null)
            return null;
        List<ContestProblemDto> contestProblemDtoList = new ArrayList<>();
        for(ContestProblem contestProblem : contestProblems){
            ContestProblemDto contestProblemDto = new ContestProblemDto();
            contestProblemDto.setId(contestProblem.getId());
            ProblemDto problemDto = Mapper.getProblemDto(contestProblem.getProblem());
            contestProblemDto.setProblemDto(problemDto);
            contestProblemDto.getProblemDto().setSubmissionCount(submissionRepository.countContestSubmissions(contest.getId(), problemDto.getId()));
            contestProblemDto.setMaxScore(contestProblem.getMaxScore());
            contestProblemDtoList.add(contestProblemDto);
        }
        return contestProblemDtoList;
    }

    @Override
    public boolean removeProblem(UserBean userBean, Integer id, Integer contestProblemId) {
        Contest contest = contestRepository.getOne(id);
        if(contest == null)
            return false;
        contestProblemRepository.deleteById(contestProblemId);
        return true;
    }

    @Override
    public ContestProblemDto getProblem(UserBean userBean, String code, String problemCode) {

        Contest contest = contestRepository.findByCode(code);
        if(contest == null)
            throw new NotFoundException("Contest not found.");
        ContestProblem contestProblem = contestProblemRepository.findByProblemCodeAndContestId(problemCode, contest.getId());
        if(contestProblem == null)
            return null;

        ContestProblemDto contestProblemDto = new ContestProblemDto();
        contestProblemDto.setId(contestProblem.getId());
        contestProblemDto.setProblemDto(problemService.getProblemByCode(userBean, contestProblem.getProblem().getCode(), false));
        contestProblemDto.setContestDto(getContestByCode(userBean, code, true));
        contestProblemDto.setMaxScore(contestProblem.getMaxScore());
        return contestProblemDto;

    }


    @Override
    public int submit(SubmissionRequest submissionRequest) throws InvalidSubmissionException, SubmissionFailedException {

        Contest contest = contestRepository.findById(submissionRequest.getContestId()).orElse(null);

        if(contest == null){
            throw  new NotFoundException("Contest not found.");
        }

        User user = userRepository.findByUsername(submissionRequest.getUserBean().getUsername());
        if(user == null){
            throw new InvalidRequestException("User not found.");
        }

        Date now = new Date();
        if(now.before(contest.getStartDate())){
            throw new InvalidSubmissionException("Contest has yet not started.");
        } else if(now.after(contest.getEndDate())){
            throw new InvalidSubmissionException("Contest has ended.");
        } else if(!registeredUserRepository.existsByUserIdAndContestId(user.getId(), contest.getId())){
            throw new InvalidSubmissionException("User should register before submitting.");
        }

        ContestProblem contestProblem = contestProblemRepository.findByProblemIdAndContestId(submissionRequest.getProblemId(), submissionRequest.getContestId());
        if(contestProblem == null)
            throw new NotFoundException("Contest problem not found.");

        submissionRequest.setProblemId(contestProblem.getProblem().getId());
        submissionRequest.setForContest(true);
        submissionRequest.setPublic(false);
        submissionRequest.setVisible(true);
        submissionRequest.setMaxScore(contestProblem.getMaxScore());
        submissionRequest.setContestId(contest.getId());
        if(contest.getContestType() == Constants.CONTEST_TYPE_CODE_IN_LESS){
            submissionRequest.setJudgeId(Constants.JUDGE_ID_CODE_IN_LESS);
        }else{
            submissionRequest.setJudgeId(Constants.JUDGE_ID_DEFAULT);
        }
        return submissionService.submit(submissionRequest);
    }

    @Override
    public Leaderboard getLeaderboard(UserBean userBean, String code, Pageable pageable) {
        Contest contest = contestRepository.findByCode(code);
        contestRepository.updateLeaderboard(contest.getId(), contest.getContestType());
        Page<RegisteredUser> registeredUserPage = registeredUserRepository.findByContestCode(code, pageable);
        Leaderboard leaderboard = new Leaderboard();
        leaderboard.setPage(registeredUserPage.map(Mapper::getLeaderboardRow));
        leaderboard.setContestDto(getContestById(userBean, contest.getId(), true));
        return leaderboard;
    }

    @Override
    @Transactional
    public void registerUser(UserBean userBean, String code) throws UserNotFoundException, NotFoundException {
        User user = userRepository.findByUsername(userBean.getUsername());

        if(user == null){
            throw new UserNotFoundException();
        }

        Contest contest = contestRepository.findByCode(code);
        if(contest == null) {
            throw new NotFoundException("Contest not found.");
        }

        Date now = new Date();
        if(!now.after(contest.getEndDate())){
            if(!registeredUserRepository.existsByUserIdAndContestId(user.getId(), contest.getId())){
                RegisteredUser registeredUser = new RegisteredUser();
                registeredUser.setContest(contest);
                registeredUser.setUser(user);
                List<RegisteredUser> registeredUsers = contest.getRegisteredUsers();
                if(registeredUsers == null){
                    contest.setRegisteredUsers(new ArrayList<>());
                }
                contest.getRegisteredUsers().add(registeredUser);
                contestRepository.save(contest);
            }
        }
    }

    @Override
    public Page<SubmissionDto> getSubmissions(UserBean userBean, String code, String problem, Pageable pageable) {
        Page<SubmissionDto> page = submissionService.getSubmissions(userBean, code, problem, pageable, true);
        for(SubmissionDto submissionDto : page.getContent()){
            if(submissionDto.getUserBean().getUsername().equals(userBean.getUsername())){
                submissionDto.setPublic(true);
            }
        }
        return page;
    }


    @Override
    public void updateContestProblems(UserBean userBean, Integer id, ProblemsUpdateForm problemsUpdateForm) throws InvalidRequestException {
        Contest contest = contestRepository.getOne(id);
        if(contest == null) {
            throw new NotFoundException("Contest not found.");
        }
        Map<Integer, ContestProblem> map = contest.getContestProblems().stream().collect(Collectors.toMap(ContestProblem::getId, contestProblem -> contestProblem));

        for(ContestProblemDto contestProblemDto : problemsUpdateForm.getProblemDetailsList()){

            ContestProblem contestProblem = map.get(contestProblemDto.getId());
            if(contestProblem == null){
                throw new InvalidRequestException();
            }
            contestProblem.setMaxScore(contestProblemDto.getMaxScore());
            contestProblemRepository.save(contestProblem);
        }

    }

    @Override
    public void updateContestSettings(UserBean userBean, Integer id, Settings settings) {
        Contest contest = contestRepository.findById(id).orElse(null);

        if(contest == null)
            throw new NotFoundException("Contest not found.");

        contest.setContestType(settings.getContestType() == 0 ? Constants.CONTEST_TYPE_DEFAULT : settings.getContestType());
        contestRepository.save(contest);
    }
}
