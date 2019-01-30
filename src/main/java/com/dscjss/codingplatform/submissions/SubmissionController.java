package com.dscjss.codingplatform.submissions;


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

    @Autowired
    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @RequestMapping(value = "/submit/{problemId}", method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> createSubmission(Principal principal, @PathVariable int problemId, @RequestBody MultipartFile source,
                                                                @RequestParam Integer compilerId){

        ResponseEntity<Map<String, String>> responseEntity;
        Map<String, String> map = new HashMap<>();
        if(principal != null){
            String username = principal.getName();
            try {
                SubmissionRequest submissionRequest = Utility.createSubmissionRequest(new UserBean(username), source, problemId, compilerId);
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
            SubmissionDto submissionDto = submissionService.getSubmission(new UserBean(username), submissionId, true);
            if(submissionDto.getResult().getStatus() == Status.RUNNING){
                submissionService.updateSubmission(submissionDto.getId());
            }
            modelAndView = new ModelAndView("submission/submission-status.html");

            modelAndView.addObject("submission", submissionDto);
            return modelAndView;

        }
        modelAndView = new ModelAndView("401.html");
        return modelAndView;
    }

    @RequestMapping(value = "/submission/{id}", method = RequestMethod.GET)
    public ModelAndView getSubmission(Principal principal, @PathVariable("id") Integer submissionId){

        ModelAndView modelAndView;

        if(principal != null) {
            String username = principal.getName();
            SubmissionDto submissionDto = submissionService.getSubmission(new UserBean(username), submissionId, false);
            if(submissionDto.getResult().getStatus() == Status.RUNNING){
                submissionService.updateSubmission(submissionDto.getId());
            }

            modelAndView = new ModelAndView("submission/submission.html");

            modelAndView.addObject("submission", submissionDto);
            return modelAndView;
        }
        modelAndView = new ModelAndView("401.html");
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
            return modelAndView;
        }
        Page<SubmissionDto> submissions = submissionService.getSubmissions(null, code, pageable);
        modelAndView.addObject("page", submissions);
        return modelAndView;
    }

}
