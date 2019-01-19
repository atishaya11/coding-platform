package com.dscjss.codingplatform.util;


import com.dscjss.codingplatform.compilers.dto.CompilerDto;
import com.dscjss.codingplatform.compilers.model.Compiler;
import com.dscjss.codingplatform.problems.dto.ProblemDto;
import com.dscjss.codingplatform.problems.dto.TestCaseDto;
import com.dscjss.codingplatform.problems.model.Problem;
import com.dscjss.codingplatform.problems.model.TestCase;
import com.dscjss.codingplatform.users.dto.UserBean;
import com.dscjss.codingplatform.users.model.User;

import java.util.ArrayList;
import java.util.List;

public class ObjectMapper {


    public static TestCaseDto getTestCaseDto(TestCase testCase){
        if(testCase == null)
            return null;

        TestCaseDto testCaseDto = new TestCaseDto();
        testCaseDto.setId(testCase.getId());
        testCaseDto.setSample(testCase.isSample());
        testCaseDto.setTag(testCase.getTag());
        return testCaseDto;
    }

    public static ProblemDto getProblemDto(Problem problem) {
        if(problem == null)
            return null;

        ProblemDto problemDto = new ProblemDto();
        problemDto.setId(problem.getId());
        problemDto.setCode(problem.getCode());
        problemDto.setName(problem.getName());
        problemDto.setAuthor(getUserBean(problem.getAuthor()));

        if(problem.getBody() != null){
            problemDto.setDescription(problem.getBody().getDescription());
            problemDto.setConstraints(problem.getBody().getConstraints());
            problemDto.setInputFormat(problem.getBody().getInputFormat());
            problemDto.setOutputFormat(problem.getBody().getOutputFormat());
        }
        return problemDto;
    }

    public static UserBean getUserBean(User user) {
        if(user == null)
            return null;

        UserBean userBean = new UserBean();
        userBean.setId(user.getId());
        userBean.setFirstName(user.getFirstName());
        userBean.setLastName(user.getLastName());
        userBean.setUsername(user.getUsername());
        return userBean;
    }

    public static List<TestCaseDto> getTestCaseDtoList(List<TestCase> testCases) {
        if(testCases == null)
            return null;
        List<TestCaseDto> testCaseDtoList = new ArrayList<>();
        testCases.forEach(testCase -> testCaseDtoList.add(getTestCaseDto(testCase)));
        return testCaseDtoList;
    }


    public static List<CompilerDto> getCompilerDtoList(List<Compiler> compilers) {

        List<CompilerDto> compilerDtoList = new ArrayList<>();
        compilers.forEach(compiler -> {
            compilerDtoList.add(getCompilerDo(compiler));
        });
        return compilerDtoList;
    }

    public static CompilerDto getCompilerDo(Compiler compiler) {
        if(compiler == null)
            return null;

        CompilerDto compilerDto = new CompilerDto();

        compilerDto.setId(compiler.getId());
        compilerDto.setName(compiler.getName());
        compilerDto.setVersion(compiler.getVersion().getName());
        compilerDto.setTimeLimit(compiler.getDefaultTimeLimit());
        return compilerDto;
    }
}
