package com.dscjss.codingplatform.submissions;


import com.dscjss.codingplatform.compilers.CompilerRepository;
import com.dscjss.codingplatform.compilers.model.Compiler;
import com.dscjss.codingplatform.problems.ProblemRepository;
import com.dscjss.codingplatform.problems.ProblemService;
import com.dscjss.codingplatform.problems.exception.TestDataDownloadException;
import com.dscjss.codingplatform.problems.model.AllowedCompiler;
import com.dscjss.codingplatform.problems.model.Problem;
import com.dscjss.codingplatform.problems.model.TestCase;
import com.dscjss.codingplatform.submissions.dto.*;
import com.dscjss.codingplatform.submissions.exception.InvalidSubmissionException;
import com.dscjss.codingplatform.submissions.exception.SubmissionFailedException;
import com.dscjss.codingplatform.submissions.model.Result;
import com.dscjss.codingplatform.submissions.model.Submission;
import com.dscjss.codingplatform.testcase.TestCaseService;
import com.dscjss.codingplatform.users.UserRepository;
import com.dscjss.codingplatform.users.dto.UserBean;
import com.dscjss.codingplatform.users.model.User;
import com.dscjss.codingplatform.util.Constants;
import com.dscjss.codingplatform.util.Mapper;
import com.dscjss.codingplatform.util.Status;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    private final Logger logger = LoggerFactory.getLogger(SubmissionServiceImpl.class);

    private final SubmissionRepository submissionRepository;
    private final CompilerRepository compilerRepository;
    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;
    private final TestCaseService testCaseService;
    private final RestTemplate restTemplate;
    private final ProblemService problemService;

    @Autowired
    public SubmissionServiceImpl(SubmissionRepository submissionRepository, CompilerRepository compilerRepository,
                                 ProblemRepository problemRepository, UserRepository userRepository,
                                 TestCaseService testCaseService, RestTemplate restTemplate, ProblemService problemService) {
        this.submissionRepository = submissionRepository;
        this.compilerRepository = compilerRepository;
        this.problemRepository = problemRepository;
        this.userRepository = userRepository;
        this.testCaseService = testCaseService;
        this.restTemplate = restTemplate;
        this.problemService = problemService;
    }

    @Override
    @Transactional
    public int submit(final SubmissionRequest submissionRequest) throws InvalidSubmissionException, SubmissionFailedException {

        if(!isValid(submissionRequest)){
            throw new InvalidSubmissionException("Invalid submission request.");
        }
        Problem problem = problemRepository.getOne(submissionRequest.getProblemId());
        if(problem == null){
            throw new InvalidSubmissionException("Invalid submission request.");
        }
        Compiler compiler = compilerRepository.getOne(submissionRequest.getCompilerId());
        if(compiler == null) {
            throw new InvalidSubmissionException("Invalid submission request.");
        }
        Set<AllowedCompiler> allowedCompilers = problem.getAllowedCompilers();
        Map<Integer, AllowedCompiler> map = allowedCompilers.stream().collect(Collectors.toMap(a -> a.getCompiler().getId(), Function.identity()));

        if(map.get(compiler.getId()) == null){
            logger.error("Submission with not allowed compiler, aborting.");
            throw new InvalidSubmissionException("Submission is not allowed for the specified compiler. ");
        }

        User user = userRepository.findByUsername(submissionRequest.getUserBean().getUsername());

        List<TestCaseDto> testCaseDtoList = new ArrayList<>();

        for(TestCase testCase : problem.getTestCases()){
            TestCaseDto testCaseDto = new TestCaseDto();
            testCaseDto.setId(testCase.getId());
            testCaseDto.setFetchData(true);
            testCaseDto.setTimeLimit(map.get(compiler.getId()).getTimeLimit());
            /*try {
                testCaseDto.setInput(testCaseService.getInputData(testCase.getId()));
                testCaseDto.setOutput(testCaseService.getOutputData(testCase.getId()));
            } catch (TestDataDownloadException e) {
                e.printStackTrace();
                throw new SubmissionFailedException("Can't create submission, failed to download tests");
            }*/
            testCaseDtoList.add(testCaseDto);
        }

        JudgeSubmissionRequest judgeSubmissionRequest = new JudgeSubmissionRequest();
        judgeSubmissionRequest.setCompilerId(compiler.getId());
        judgeSubmissionRequest.setJudgeId(problem.getJudgeId());
        judgeSubmissionRequest.setSource(submissionRequest.getSource());
        judgeSubmissionRequest.setTestCaseList(testCaseDtoList);


        String url = Constants.JUDGE_API_ENDPOINT + "/submission";
        HttpEntity<JudgeSubmissionRequest> judgeSubmissionRequestHttpEntity = new HttpEntity<>(judgeSubmissionRequest);

        ResponseEntity<Integer> responseEntity = restTemplate.exchange(url, HttpMethod.POST, judgeSubmissionRequestHttpEntity, Integer.class);

        if(responseEntity.getStatusCode() == HttpStatus.OK){
            logger.info("Status OK from judge. ");
            Integer judgeId = responseEntity.getBody();
            if(judgeId == null){
                throw new SubmissionFailedException("Unable to submit, try again.");
            }
            logger.info("Judge id for new submission - {}", judgeId);
            Submission submission = new Submission();
            submission.setUser(user);
            submission.setCompiler(compiler);
            submission.setProblem(problem);
            submission.setPublic(submissionRequest.isPublic());
            submission.setVisible(submissionRequest.isVisible());
            submission.setForContest(submissionRequest.isForContest());
            submission.setCreationDate(new Date());
            submission.setRemoteId(judgeId);
            Result result = new Result();
            result.setStatus(Status.RUNNING);
            submission.setResult(result);
            submissionRepository.save(submission);
            return submission.getId();
        }else{
            throw new SubmissionFailedException("Unable to submit, try again.");
        }

    }

    //TODO Implement the below method
    private boolean isValid(SubmissionRequest submissionRequest) {
        return true;
    }

    @Override
    @Cacheable(value = "submissions", key = "{#submissionId, #onlySummary}", unless = "#result.result.status.name() == 'RUNNING'")
    public SubmissionDto getSubmission(UserBean userBean, Integer submissionId, boolean onlySummary) {
        logger.info("Check - Is cached submission.");
        if(submissionId == null)
            return null;
        Submission submission = submissionRepository.getOne(submissionId);
        if(submission == null)
            return null;
        return getSubmission(userBean, submission, onlySummary);
    }

    private SubmissionDto getSubmission(UserBean userBean, Submission submission, boolean onlySummary){
        SubmissionDto submissionDto = Mapper.getSubmissionDto(submission);
        submissionDto.setProblemDto(problemService.getProblemById(userBean, submission.getProblem().getId(), true));
        if(onlySummary){
            return submissionDto;
        }else{
            if(submissionDto.getResult().getStatus() == Status.RUNNING){
                return submissionDto;
            }else{
                String url = Constants.JUDGE_API_ENDPOINT + "submission/" + submission.getRemoteId() + "?withSource=true";
                final String json= restTemplate.getForObject(url, String.class);
                try {
                    submissionDto.setSource(getSourceFromJson(json));

                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("Error fetching source code.");
                }
                try {
                    List<TestCaseResult> testCaseResults = getTestCaseResultsFromJson(json);
                    submissionDto.setTestCaseResultList(testCaseResults);
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("Error fetching test case results.");
                }
                return submissionDto;
            }
        }

    }


    private String getSourceFromJson(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        final ObjectNode objectNode = objectMapper.readValue(json, ObjectNode.class);
        String source = "";
        if(objectNode.has("source")){
            source = objectNode.get("source").asText();
        }
        return source;
    }

    private List<TestCaseResult> getTestCaseResultsFromJson(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.readValue(json, ObjectNode.class);
        if(objectNode.has("testCaseResultList")){
            final String testCaseResultListString = objectNode.get("testCaseResultList").toString();
            final List<TestCaseResult> testCaseResults = objectMapper.readValue(testCaseResultListString, new TypeReference<List<TestCaseResult>>(){});
            return testCaseResults;
        }
        return null;
    }


    @Override
    @Async
    @Transactional
    public void updateSubmission(int id) {
        Submission submission = submissionRepository.getOne(id);
        String url = Constants.JUDGE_API_ENDPOINT + "/submission/" + submission.getRemoteId();
        String json = restTemplate.getForObject(url, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        final ObjectNode objectNode;
        try {
            objectNode = objectMapper.readValue(json, ObjectNode.class);
            if(objectNode.has("executing")){
                final boolean executing = objectNode.get("executing").asBoolean();
                if(!executing){
                    Result result = submission.getResult();
                    JsonNode jsonNode = objectNode.get("result");
                    result.setStatus(Status.valueOf(jsonNode.get("status").asText()));
                    result.setMemory(jsonNode.get("memory").asDouble());
                    result.setScore(jsonNode.get("score").asDouble());
                    result.setTime(jsonNode.get("time").asInt());
                    submissionRepository.save(submission);
                }
            }
        } catch (IOException e) {
            logger.error("Error parsing the response object for submission.");
            e.printStackTrace();
        }


    }

    @Override
    public Page<SubmissionDto> getSubmissions(UserBean userBean, String code, Pageable pageable) {
        Page<Submission> submissionPage = submissionRepository.findByProblemCode(code, pageable);
        return submissionPage.map(submission -> getSubmission(userBean, submission, true));
    }
}
