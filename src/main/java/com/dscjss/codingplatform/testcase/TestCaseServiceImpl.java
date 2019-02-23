package com.dscjss.codingplatform.testcase;

import com.dscjss.codingplatform.problems.exception.TestDataDownloadException;
import com.dscjss.codingplatform.util.FileManager;
import org.apache.commons.codec.Charsets;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class TestCaseServiceImpl implements TestCaseService {

    private final Logger logger = LoggerFactory.getLogger(TestCaseServiceImpl.class);

    private final FileManager fileManager;
    private final TestCaseRepository testCaseRepository;

    @Autowired
    public TestCaseServiceImpl(FileManager fileManager, TestCaseRepository testCaseRepository) {
        this.fileManager = fileManager;
        this.testCaseRepository = testCaseRepository;
    }


    @Override
    @Cacheable("test_cases_input")
    public String getInputData(Integer id) throws TestDataDownloadException {
        String file = id + "/input.txt";
        return fetchTestData(file);
    }

    @Override
    @Cacheable("test_cases_output")
    public String getOutputData(Integer id) throws TestDataDownloadException {
        String file = id + "/output.txt";
        return fetchTestData(file);
    }


    private String fetchTestData(String file) throws TestDataDownloadException {
        return getFileData(file);
    }


    private String getFileData(String fileName) throws TestDataDownloadException {
        try {
            File file = fileManager.downloadTestDataFile(fileName);
            return FileUtils.readFileToString(file, Charsets.UTF_8.name());
        }catch (InterruptedException | IOException e){
            throw new TestDataDownloadException("Test data download failed.");
        }
    }

    @Override
    public void deleteTestCase(int testCaseId) {
        String inputFile = testCaseId + "/input.txt";
        String outputFile = testCaseId + "/output.txt";
        fileManager.deleteTestDataFiles(new String[]{inputFile, outputFile});
        testCaseRepository.deleteById(testCaseId);
    }
}
