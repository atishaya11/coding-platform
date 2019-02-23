package com.dscjss.codingplatform.problems;

import com.dscjss.codingplatform.compilers.dto.CompilerDto;
import com.dscjss.codingplatform.problems.dto.ProblemDto;
import com.dscjss.codingplatform.problems.dto.TestCaseDto;
import com.dscjss.codingplatform.problems.dto.UploadTestCaseDto;
import com.dscjss.codingplatform.problems.exception.TestDataUploadException;
import com.dscjss.codingplatform.users.dto.UserBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProblemService {

    ProblemDto getProblemByCode(UserBean userBean, String code, boolean onlySummary);

    ProblemDto getProblemById(UserBean userBean, Integer id, boolean onlySummary);

    Page<ProblemDto> getProblems(UserBean userBean, Pageable pageable);

    void updateProblemDetails(UserBean userBean, ProblemDto problem, Integer id);

    TestCaseDto uploadTestCase(UserBean userBean, UploadTestCaseDto testCaseDto, Integer id) throws TestDataUploadException;

    ProblemDto createProblemFromName(UserBean userBean, String name);

    List<ProblemDto> getProblemsByAuthor(UserBean userBean);

    List<CompilerDto> getCompilerList(Integer problemId);

    List<CompilerDto> getCompilerList();

    void updateAllowedCompilers(UserBean userBean, List<CompilerDto> compilers, Integer id);

    void addToPractice(UserBean userBean, int problemId);

    void removeFromPractice(UserBean userBean, int id);
}
