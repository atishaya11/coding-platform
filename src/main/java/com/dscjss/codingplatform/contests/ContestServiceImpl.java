package com.dscjss.codingplatform.contests;

import com.dscjss.codingplatform.compilers.dto.CompilerDto;
import com.dscjss.codingplatform.contests.dto.ContestDto;
import com.dscjss.codingplatform.contests.dto.ContestProblemDto;
import com.dscjss.codingplatform.contests.exception.NotFoundException;
import com.dscjss.codingplatform.contests.model.Contest;
import com.dscjss.codingplatform.contests.model.ContestBody;
import com.dscjss.codingplatform.contests.model.ContestProblem;
import com.dscjss.codingplatform.contests.model.RegisteredUser;
import com.dscjss.codingplatform.error.InvalidRequestException;
import com.dscjss.codingplatform.error.UserNotFoundException;
import com.dscjss.codingplatform.problems.ProblemRepository;
import com.dscjss.codingplatform.problems.ProblemService;
import com.dscjss.codingplatform.problems.dto.ProblemDto;
import com.dscjss.codingplatform.problems.model.Problem;
import com.dscjss.codingplatform.submissions.SubmissionService;
import com.dscjss.codingplatform.submissions.dto.SubmissionRequest;
import com.dscjss.codingplatform.submissions.exception.InvalidSubmissionException;
import com.dscjss.codingplatform.submissions.exception.SubmissionFailedException;
import com.dscjss.codingplatform.submissions.model.Submission;
import com.dscjss.codingplatform.users.UserRepository;
import com.dscjss.codingplatform.users.dto.UserBean;
import com.dscjss.codingplatform.users.model.User;
import com.dscjss.codingplatform.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ContestServiceImpl implements ContestService {

    private ContestRepository contestRepository;
    private UserRepository userRepository;
    private ProblemRepository problemRepository;
    private ContestProblemRepository contestProblemRepository;
    private ProblemService problemService;
    private SubmissionService submissionService;
    private RegisteredUserRepository registeredUserRepository;

    @Autowired
    public ContestServiceImpl(ContestRepository contestRepository, UserRepository userRepository, ProblemRepository problemRepository, ContestProblemRepository contestProblemRepository, ProblemService problemService, SubmissionService submissionService, RegisteredUserRepository registeredUserRepository) {
        this.contestRepository = contestRepository;
        this.userRepository = userRepository;
        this.problemRepository = problemRepository;
        this.contestProblemRepository = contestProblemRepository;
        this.problemService = problemService;
        this.submissionService = submissionService;
        this.registeredUserRepository = registeredUserRepository;
    }

    @Override
    public ContestDto getContestById(UserBean userBean, Integer id, boolean onlySummary) {
        return Mapper.getContestDto(contestRepository.getOne(id));
    }

    @Override
    public ContestDto getContestByCode(UserBean userBean, String code, boolean onlySummary) {
        Contest contest = contestRepository.findByCode(code);
        ContestDto contestDto = Mapper.getContestDto(contest);
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
        contest.setOwner(user);
        contest.setCreationDate(new Date());
        contest.setLastModifiedDate(new Date());
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
        List<Contest> contests = contestRepository.findByOwnerId(userBean.getId());
        List<ContestDto> contestDtoList = new ArrayList<>();
        contests.forEach(contest -> {
            contestDtoList.add(Mapper.getContestDto(contest));
        });
        return contestDtoList;
    }

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
        Problem problem = problemRepository.getOne(problemId);
        if(problem == null)
            return false;

        Contest contest = contestRepository.getOne(id);
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
            contestProblemDto.setProblemDto(Mapper.getProblemDto(contestProblem.getProblem()));
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
            throw new NotFoundException();
        ContestProblem contestProblem = contestProblemRepository.findByProblemCode(problemCode);
        if(contestProblem == null)
            return null;

        ContestProblemDto contestProblemDto = new ContestProblemDto();
        contestProblemDto.setId(contestProblem.getId());
        contestProblemDto.setProblemDto(problemService.getProblemByCode(userBean, contestProblem.getProblem().getCode(), false));
        contestProblemDto.setContestDto(Mapper.getContestDto(contest));
        return contestProblemDto;

    }


    @Override
    public int submit(SubmissionRequest submissionRequest) throws InvalidSubmissionException, SubmissionFailedException {

        Contest contest = contestRepository.getOne(submissionRequest.getContestId());
        if(contest == null){
            throw  new InvalidSubmissionException("Invalid contest.");
        }
        ContestProblem contestProblem = contestProblemRepository.getOne(submissionRequest.getContestProblemId());
        if(contestProblem == null)
            throw new InvalidSubmissionException("Invalid contest problem");
        submissionRequest.setProblemId(contestProblem.getProblem().getId());
        submissionRequest.setForContest(true);
        submissionRequest.setPublic(false);
        submissionRequest.setMaxScore(contestProblem.getMaxScore());
        return submissionService.submit(submissionRequest);

    }

    @Override
    public Page<RegisteredUser> getLeaderboard(UserBean userBean, String code, Pageable pageable) {
        return registeredUserRepository.findByContestCode(code, pageable);
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
            throw new NotFoundException();
        }

        if(!registeredUserRepository.existsByUserId(user.getId())){
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
