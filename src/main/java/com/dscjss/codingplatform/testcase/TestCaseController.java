package com.dscjss.codingplatform.testcase;

import com.dscjss.codingplatform.problems.exception.TestDataDownloadException;
import com.dscjss.codingplatform.util.Constants;
import org.apache.tomcat.util.bcel.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class TestCaseController {

    private Logger logger = LoggerFactory.getLogger(TestCaseController.class);


    private final TestCaseService testCaseService;

    @Autowired
    public TestCaseController(TestCaseService testCaseService) {
        this.testCaseService = testCaseService;
    }

    @ResponseBody
    @RequestMapping(value = "/test_case/{id}/input", method = RequestMethod.GET)
    public ResponseEntity<String> getInputTestData(@PathVariable Integer id, @RequestParam String token){
        ResponseEntity<String> responseEntity;
        if(!token.equals(Constants.AUTH_TOKEN)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            responseEntity = new ResponseEntity<>(testCaseService.getInputData(id), HttpStatus.OK);
            return responseEntity;
        } catch (TestDataDownloadException e) {
            e.printStackTrace();
            logger.error("Unable to get test data for id {}", id);
            responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }
    @ResponseBody
    @RequestMapping(value = "/test_case/{id}/output", method = RequestMethod.GET)
    public ResponseEntity<String> getOutputTestData(@PathVariable Integer id, @RequestParam String token){
        if(!token.equals(Constants.AUTH_TOKEN)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            return new ResponseEntity<>(testCaseService.getOutputData(id), HttpStatus.OK);
        } catch (TestDataDownloadException e) {
            e.printStackTrace();
            logger.error("Unable to get test data for id {}", id);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
