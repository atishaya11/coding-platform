package com.dscjss.codingplatform.problems;


import com.dscjss.codingplatform.compilers.CompilerRepository;
import com.dscjss.codingplatform.compilers.dto.CompilerDto;
import com.dscjss.codingplatform.compilers.model.Compiler;
import com.dscjss.codingplatform.contests.exception.NotFoundException;
import com.dscjss.codingplatform.problems.dto.ProblemDto;
import com.dscjss.codingplatform.problems.dto.TestCaseDto;
import com.dscjss.codingplatform.problems.dto.UploadTestCaseDto;
import com.dscjss.codingplatform.problems.exception.TestDataUploadException;
import com.dscjss.codingplatform.problems.exception.TestDataDownloadException;
import com.dscjss.codingplatform.problems.model.AllowedCompiler;
import com.dscjss.codingplatform.problems.model.Problem;
import com.dscjss.codingplatform.problems.model.ProblemBody;
import com.dscjss.codingplatform.problems.model.TestCase;
import com.dscjss.codingplatform.submissions.SubmissionRepository;
import com.dscjss.codingplatform.submissions.model.Result;
import com.dscjss.codingplatform.testcase.TestCaseRepository;
import com.dscjss.codingplatform.testcase.TestCaseService;
import com.dscjss.codingplatform.users.UserRepository;
import com.dscjss.codingplatform.users.dto.UserBean;
import com.dscjss.codingplatform.users.model.User;
import com.dscjss.codingplatform.util.FileManager;
import com.dscjss.codingplatform.util.Mapper;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProblemServiceImpl implements ProblemService {


    private final Logger logger = LoggerFactory.getLogger(ProblemServiceImpl.class);

    private final ProblemRepository problemRepository;
    private final CompilerRepository compilerRepository;
    private final FileManager fileManager;
    private final TestCaseRepository testCaseRepository;
    private final UserRepository userRepository;
    private final AllowedCompilerRepository allowedCompilerRepository;
    private final TestCaseService testCaseService;
    private final CacheManager cacheManager;
    private final SubmissionRepository submissionRepository;

    @Autowired
    public ProblemServiceImpl(ProblemRepository problemRepository, CompilerRepository compilerRepository, FileManager fileManager, TestCaseRepository testCaseRepository, UserRepository userRepository, AllowedCompilerRepository allowedCompilerRepository, TestCaseService testCaseService, CacheManager cacheManager, SubmissionRepository submissionRepository) {
        this.problemRepository = problemRepository;
        this.compilerRepository = compilerRepository;
        this.fileManager = fileManager;
        this.testCaseRepository = testCaseRepository;
        this.userRepository = userRepository;
        this.allowedCompilerRepository = allowedCompilerRepository;
        this.testCaseService = testCaseService;
        this.cacheManager = cacheManager;
        this.submissionRepository = submissionRepository;
    }

    @Override
    @Cacheable(value = "problems", key = "#code + #onlySummary")
    public ProblemDto getProblemByCode(UserBean userBean, String code, boolean onlySummary) {
        logger.info("Check - Is cached problem.");
        Problem problem = problemRepository.findByCode(code);
        ProblemDto problemDto = Mapper.getProblemDto(problem);
        if(onlySummary){
            return problemDto;
        }else{
            List<CompilerDto> compilerDtoList = getCompilerList(problemDto.getId());
            problemDto.setCompilers(compilerDtoList);
            problemDto.setTestCaseDtoList(getSampleTests(problem));
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            for(CompilerDto compiler : compilerDtoList){
                min = Math.min(compiler.getTimeLimit(), min);
                max = Math.max(compiler.getTimeLimit(), max);
            }
            problemDto.setMinTimeLimit(min);
            problemDto.setMaxTimeLimit(max);
        }
        return problemDto;
    }

    private List<TestCaseDto> getSampleTests(Problem problem){
        List<TestCase> testCases = problem.getTestCases();
        List<TestCaseDto> testCaseDtoList = new ArrayList<>();
        for(TestCase testCase : testCases){
            if(testCase.isSample()){
                try {

                    String input = testCaseService.getInputData(testCase.getId());
                    String output = testCaseService.getOutputData(testCase.getId());
                    TestCaseDto testCaseDto = Mapper.getTestCaseDto(testCase);
                    testCaseDto.setInput(input);
                    testCaseDto.setOutput(output);
                    testCaseDtoList.add(testCaseDto);
                }catch (TestDataDownloadException e){
                    logger.error("Unable to download test data for test case {}", testCase.getId());
                }
            }
        }
        return testCaseDtoList;
    }
    @Override
    public ProblemDto getProblemById(UserBean userBean, Integer id, boolean onlySummary) {
        Problem problem = problemRepository.getOne(id);
        ProblemDto problemDto = Mapper.getProblemDto(problem);
        if(onlySummary){
            return problemDto;
        }else{
            List<TestCase> testCases = problem.getTestCases();
            List<TestCaseDto> testCaseDtoList = new ArrayList<>();
            testCases.forEach(testCase -> {
                testCaseDtoList.add(Mapper.getTestCaseDto(testCase));
            });
            problemDto.setTestCaseDtoList(testCaseDtoList);
        }
        return problemDto;
    }



    @Override
    public Page<ProblemDto> getProblems(UserBean userBean, Pageable pageable) {
        Page<Problem> page = problemRepository.findAllByPracticeIsTrue(pageable);
        return page.map(problem -> {
            ProblemDto problemDto = Mapper.getProblemDto(problem);
            problemDto.setSubmissionCount(submissionRepository.countPracticeSubmissions(problem.getId()));
            return problemDto;
        });
    }

    @Override
    @Transactional
    public void updateProblemDetails(UserBean userBean, ProblemDto problemDto, Integer id) {
        Problem problem = problemRepository.getOne(id);

        Date now = new Date();
        long diff = now.getTime() - problem.getCreationDate().getTime();
        if(diff < 2 * 24 * 60 * 60 * 1000){
            problem.setCode(problemDto.getCode());
        }

        problem.setName(problemDto.getName());
        ProblemBody problemBody = problem.getBody();
        if(problemBody == null){
            problemBody = new ProblemBody();
        }
        problemBody.setDescription(problemDto.getDescription());
        problemBody.setInputFormat(problemDto.getInputFormat());
        problemBody.setOutputFormat(problemDto.getOutputFormat());
        problemBody.setConstraints(problemDto.getConstraints());
        problemBody.setExplanation(problemDto.getExplanation());

        problem.setBody(problemBody);
        problem.setModificationDate(new Date());
        problemRepository.save(problem);

        cacheEvict("problems", problem.getCode() + "" + false);
        cacheEvict("problems", problem.getCode() + "" + true);
    }

    @Override
    @Transactional(rollbackFor = TestDataUploadException.class)
    public TestCaseDto uploadTestCase(UserBean userBean, UploadTestCaseDto testCaseDto, Integer id) throws TestDataUploadException {
        Problem problem = problemRepository.getOne(id);
        TestCase testCase = new TestCase();
        testCase.setProblem(problem);
        testCase.setSample(testCaseDto.isSample());
        testCase.setTag(testCaseDto.getTag());
        testCase.setCreationDate(new Date());
        testCase.setModificationDate(new Date());

        testCaseRepository.save(testCase);

        String inputFileName =  testCase.getId() + "/" + "input.txt";
        String outputFileName = testCase.getId() + "/" + "output.txt";

        try {
            File inputFile = convertMultiPartToFile(testCaseDto.getInputFile(), testCase.getId());
            uploadFile(inputFile, inputFileName);
            File outputFile = convertMultiPartToFile(testCaseDto.getOutputFile(), testCase.getId());
            uploadFile(outputFile, outputFileName);
        } catch (IOException | FileUploadException e) {
            e.printStackTrace();
            logger.error("Unable to create test case for problem {} .", problem.getId());
            throw new TestDataUploadException("Test case creation failed.");
        }

        if(problem.getTestCases() == null){
            problem.setTestCases(new ArrayList<>());
        }
        problem.getTestCases().add(testCase);
        problemRepository.save(problem);
        cacheEvict("problems", problem.getCode() + "" + false);
        return Mapper.getTestCaseDto(testCase);
    }



    private void uploadFile(File file, String fileName) throws FileUploadException {
        fileManager.uploadTestDataFile(fileName, file);
        boolean delete = file.delete();
    }

    private File convertMultiPartToFile(MultipartFile multipartFile, Integer id) throws IOException {
        String path = System.getProperty("java.io.tmpdir") + "/test/" + id;
        File dir = new File(path);
        dir.mkdirs();
        File file = new File(dir.getAbsolutePath() +"/" + System.currentTimeMillis());
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(multipartFile.getBytes());
        fileOutputStream.close();
        return file;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ProblemDto createProblemFromName(UserBean userBean, String name) {
            User user = userRepository.findByUsername(userBean.getUsername());
            String tempCode = createTempCode(name.trim());
            Problem problem = new Problem();
            problem.setName(name);
            problem.setAuthor(user);
            problem.setJudgeId(1);
            problem.setCreationDate(new Date());
            problem.setModificationDate(new Date());
            problem.setPractice(false);
            Integer countSimilar = problemRepository.countByCodeIsStartingWith(tempCode);
            if(countSimilar != 0)
                tempCode += "-"+countSimilar + 1;
            problem.setCode(tempCode);
            problemRepository.save(problem);
            return Mapper.getProblemDto(problem);
    }

    private String createTempCode(String name){
        String lowerCaseTitle = name.toLowerCase();
        lowerCaseTitle = lowerCaseTitle.replace("[^A-Za-z0-9 ]", "");
        return lowerCaseTitle.replace(' ', '-');
    }

    @Override
    public List<ProblemDto> getProblemsByAuthor(UserBean userBean) {
        List<Problem> problems = problemRepository.findByAuthor_Id(userBean.getId());
        List<ProblemDto> problemDtoList = new ArrayList<>();
        problems.forEach(problem -> {
            problemDtoList.add(Mapper.getProblemDto(problem));
        });
        return problemDtoList;
    }
    //TODO Fetch compilers from judge api
    private List<Compiler> getAllCompilers() {
        return compilerRepository.findAll();
    }


    @Override
    public List<CompilerDto> getCompilerList(Integer problemId) {
        Set<AllowedCompiler> allowedCompilers = problemRepository.getOne(problemId).getAllowedCompilers();
        if(allowedCompilers == null){
            return getCompilerList();
        }
        Map<Integer, AllowedCompiler> map = allowedCompilers.stream().
                collect(Collectors.toMap(a -> a.getCompiler().getId(), Function.identity()));

        List<Compiler> compilers = getAllCompilers();
        List<CompilerDto> compilerDtoList = new ArrayList<>();

        for(Compiler compiler : compilers){
            CompilerDto compilerDto = Mapper.getCompilerDo(compiler);
            if(map.get(compiler.getId()) != null){
                compilerDto.setTimeLimit(map.get(compiler.getId()).getTimeLimit());
                compilerDto.setAllowed(true);
                compilerDtoList.add(compilerDto);
            }else{
                compilerDtoList.add(compilerDto);
            }
        }
        return compilerDtoList;
    }

    @Override
    public List<CompilerDto> getCompilerList() {
        return Mapper.getCompilerDtoList(getAllCompilers());
    }

    @Override
    @Transactional
    public void updateAllowedCompilers(UserBean userBean, List<CompilerDto> compilers, Integer problemId) {
        if(compilers == null || compilers.size() < 1)
            return;

        Problem problem = problemRepository.getOne(problemId);

        if(problem.getAllowedCompilers() == null){
            problem.setAllowedCompilers(new HashSet<>());
        }

        Map<Integer, AllowedCompiler> map = problem.getAllowedCompilers().stream().
                collect(Collectors.toMap(a -> a.getCompiler().getId(), Function.identity()));

        for(CompilerDto compilerDto : compilers){
            int id = compilerDto.getId();
            if(map.get(id) == null){
                if(compilerDto.isAllowed()){
                    AllowedCompiler allowedCompiler = new AllowedCompiler();
                    Compiler compiler = compilerRepository.getOne(id);
                    allowedCompiler.setCompiler(compiler);
                    allowedCompiler.setProblem(problem);
                    allowedCompiler.setTimeLimit(compilerDto.getTimeLimit() == 0 ? compiler.getDefaultTimeLimit() : compilerDto.getTimeLimit());
                    allowedCompilerRepository.save(allowedCompiler);
                }
            }else{
                if(compilerDto.isAllowed()){
                    AllowedCompiler allowedCompiler = map.get(id);
                    allowedCompiler.setTimeLimit(compilerDto.getTimeLimit());
                }else{
                    allowedCompilerRepository.delete(map.get(id));
                    problem.getAllowedCompilers().remove(map.get(id));
                    problemRepository.save(problem);
                }
            }
        }


        cacheEvict("problems", problem.getCode() + "" + false);
    }

    private void cacheEvict(String name, String key) {

        cacheManager.getCache(name).evict(key);

        cacheManager.getCache(name).evict(key);
    }

    @Override
    @Transactional
    public void addToPractice(UserBean userBean, int problemId) {
        problemRepository.setPracticeById(true, problemId);
    }

    @Override
    @Transactional
    public void removeFromPractice(UserBean userBean, int problemId) {
        problemRepository.setPracticeById(false, problemId);
    }

    @Override
    @Transactional
    public void deleteTestCase(UserBean userBean, int id, int testCaseId) {
        Problem problem = problemRepository.findById(id).orElse(null);
        if(problem == null)
            throw new NotFoundException("Problem not found.");
        TestCase toDelete = null;
        for(TestCase testCase : problem.getTestCases()){
            if(testCase.getId() == testCaseId){
               toDelete = testCase;
            }
        }
        if(toDelete != null){
            testCaseService.deleteTestCase(testCaseId);
            problem.getTestCases().remove(toDelete);
            problemRepository.save(problem);
        }
    }
}
