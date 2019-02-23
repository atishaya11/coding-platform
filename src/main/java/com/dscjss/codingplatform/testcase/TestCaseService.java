package com.dscjss.codingplatform.testcase;

import com.dscjss.codingplatform.problems.exception.TestDataDownloadException;

public interface TestCaseService {
    String getInputData(Integer id) throws TestDataDownloadException;

    String getOutputData(Integer id) throws TestDataDownloadException;

    void deleteTestCase(int testCaseId);
}
