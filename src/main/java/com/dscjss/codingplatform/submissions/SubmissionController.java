package com.dscjss.codingplatform.submissions;


import com.dscjss.codingplatform.contests.exception.NotFoundException;
import com.dscjss.codingplatform.error.InvalidRequestException;
import com.dscjss.codingplatform.problems.ProblemService;
import com.dscjss.codingplatform.submissions.dto.SubmissionRequest;
import com.dscjss.codingplatform.submissions.dto.SubmissionDto;
import com.dscjss.codingplatform.submissions.exception.InvalidSubmissionException;
import com.dscjss.codingplatform.submissions.exception.SubmissionFailedException;
import com.dscjss.codingplatform.users.dto.UserBean;
import com.dscjss.codingplatform.util.Status;
import com.dscjss.codingplatform.util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static com.dscjss.codingplatform.util.Utility.createPageable;

@Controller
public class SubmissionController {

    private final Logger logger = LoggerFactory.getLogger(SubmissionController.class);

    private final SubmissionService submissionService;
    private final ProblemService problemService;

    @Autowired
    public SubmissionController(SubmissionService submissionService, ProblemService problemService) {
        this.submissionService = submissionService;
        this.problemService = problemService;
    }


    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFoundException() {
        throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ModelAndView handleInvalidRequestException() {
        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/submit/{problemId}", method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> createSubmission(Principal principal, @PathVariable int problemId, @RequestBody MultipartFile sourceFile,
                                                                @RequestParam Integer compilerId, @RequestParam(required = false) String source){

        ResponseEntity<Map<String, String>> responseEntity;
        Map<String, String> map = new HashMap<>();
        if(principal != null){
            String username = principal.getName();
            try {
                SubmissionRequest submissionRequest = Utility.createSubmissionRequest(new UserBean(username), sourceFile, source, problemId, compilerId);
                submissionRequest.setVisible(true);
                submissionRequest.setPublic(true);
                int submissionId = submissionService.submit(submissionRequest);
                map.put("submission_id", String.valueOf(submissionId));
                responseEntity = new ResponseEntity<>(map, HttpStatus.CREATED);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("Error creating submission.");
                responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } catch (InvalidSubmissionException e) {
                e.printStackTrace();
                logger.error("Error creating submission, invalid submission request.");
                responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } catch (SubmissionFailedException e) {
                logger.error("Error creating submission, submission failed exception");
                responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            responseEntity = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return responseEntity;
    }

    @RequestMapping(value = "/submit/status/{id}", method = RequestMethod.GET)
    public ModelAndView getSubmissionStatus(Principal principal, @PathVariable("id") Integer submissionId){

        ModelAndView modelAndView;

        if(principal != null) {
            String username = principal.getName();
            SubmissionDto submissionDto;
            submissionDto = submissionService.getSubmission(new UserBean(username), submissionId, true);
            if(submissionDto.getResult().getStatus() == Status.RUNNING){
                submissionService.updateSubmission(submissionDto.getId());
            }
            modelAndView = new ModelAndView("submission/submission-status.html");

            modelAndView.addObject("submission", submissionDto);
            return modelAndView;

        }
        return new ModelAndView("error/401.html");
    }

    @RequestMapping(value = "/submission/{id}", method = RequestMethod.GET)
    public ModelAndView getSubmission(Principal principal, @PathVariable("id") Integer submissionId){

        String username = null;
        SubmissionDto submissionDto;
        if(principal != null){
            username = principal.getName();
            submissionDto = submissionService.getSubmission(new UserBean(username), submissionId, false);
        } else{
            submissionDto = submissionService.getSubmission(null, submissionId, false);
        }

        if(submissionDto.getResult().getStatus() == Status.RUNNING) {
            submissionService.updateSubmission(submissionDto.getId());
        }

        if(!submissionDto.isPublic() && !submissionDto.getUserBean().getUsername().equals(username)){
            return new ModelAndView("error/403.html");
        }

        ModelAndView modelAndView = new ModelAndView("submission/submission.html");
        modelAndView.addObject("submission", submissionDto);
        return modelAndView;

    }
    @RequestMapping(value = {"/status/{code}/", "/status/{code}"})
    public ModelAndView submissions(Principal principal, @PathVariable String code, Integer page,
                                    @RequestParam(name = "sort_by", required = false, defaultValue = "creationDate") String sort,
                                    @RequestParam(name = "sort_order", required = false, defaultValue = "desc") String order) {
        ModelAndView modelAndView = new ModelAndView("submission/submissions.html");
        int pageSize = 20;
        Pageable pageable = createPageable(page == null ? 0 : page, sort, order, pageSize);
        if (principal != null) {
            String username = principal.getName();
            Page<SubmissionDto> submissions = submissionService.getSubmissions(new UserBean(username), code, pageable);
            modelAndView.addObject("page", submissions);
            modelAndView.addObject("problem", problemService.getProblemByCode(new UserBean(username), code, true));
            return modelAndView;
        }
        Page<SubmissionDto> submissions = submissionService.getSubmissions(null, code, pageable);
        modelAndView.addObject("page", submissions);
        modelAndView.addObject("problem", problemService.getProblemByCode(null, code, true));
        return modelAndView;
    }

    @RequestMapping(value = {"/status/{code}/{user}"})
    public ModelAndView userSubmissions(Principal principal, @PathVariable String code, @PathVariable String user, Integer page,
                                    @RequestParam(name = "sort_by", required = false, defaultValue = "creationDate") String sort,
                                    @RequestParam(name = "sort_order", required = false, defaultValue = "desc") String order) {
        ModelAndView modelAndView = new ModelAndView("submission/submissions.html");
        int pageSize = 20;
        Pageable pageable = createPageable(page == null ? 0 : page, sort, order, pageSize);
        if (principal != null) {
            String username = principal.getName();
            Page<SubmissionDto> submissions = submissionService.getSubmissionsByUser(new UserBean(username), code, pageable, user);
            modelAndView.addObject("page", submissions);
            modelAndView.addObject("problem", problemService.getProblemByCode(new UserBean(username), code, true));
            return modelAndView;
        }
        return new ModelAndView("error/404.html");
    }


    @RequestMapping(value = "/executing/status/{id}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, String>> isExecuting(Principal principal, @PathVariable Integer id){

        ResponseEntity<Map<String, String>> responseEntity;
        Map<String, String> map = new HashMap<>();
        String username = principal.getName();
        SubmissionDto submissionDto = submissionService.getSubmission(new UserBean(username), id, true);
        map.put("status", submissionDto.getResult().getStatus().name());
        responseEntity = new ResponseEntity<>(map, HttpStatus.OK);
        if(submissionDto.getResult().getStatus() == Status.RUNNING){
            submissionService.updateSubmission(submissionDto.getId());
        }
        return responseEntity;

    }

}
