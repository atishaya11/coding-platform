package com.dscjss.codingplatform.contests;


import com.dscjss.codingplatform.contests.dto.ContestDto;
import com.dscjss.codingplatform.contests.dto.ContestProblemDto;
import com.dscjss.codingplatform.contests.dto.Leaderboard;
import com.dscjss.codingplatform.contests.exception.NotFoundException;
import com.dscjss.codingplatform.contests.model.RegisteredUser;
import com.dscjss.codingplatform.problems.dto.ProblemDto;
import com.dscjss.codingplatform.submissions.dto.SubmissionDto;
import com.dscjss.codingplatform.submissions.dto.SubmissionRequest;
import com.dscjss.codingplatform.submissions.exception.InvalidSubmissionException;
import com.dscjss.codingplatform.submissions.exception.SubmissionFailedException;
import com.dscjss.codingplatform.users.dto.UserBean;
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
import java.util.List;
import java.util.Map;

import static com.dscjss.codingplatform.util.Utility.createPageable;

@Controller
public class ContestController {

    private final Logger logger = LoggerFactory.getLogger(ContestController.class);

    private ContestService contestService;

    @Autowired
    public ContestController(ContestService contestService) {
        this.contestService = contestService;
    }


    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFoundException() {
        throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/contests")
    public ModelAndView contests(Principal principal){
        ModelAndView modelAndView = new ModelAndView("contest/contests.html");
        String username = null;
        if(principal != null){
            username = principal.getName();
        }
        modelAndView.addObject("contests", contestService.getContests(new UserBean(username)));
        return modelAndView;
    }

    @GetMapping("/contests/{code}")
    public ModelAndView contest(Principal principal, @PathVariable String code){
        ModelAndView modelAndView = new ModelAndView("contest/landing.html");
        String username = null;

        if(principal != null){
            username = principal.getName();
        }

        ContestDto contestDto = contestService.getContestByCode(new UserBean(username), code, false);

        modelAndView.addObject("contest", contestDto);
        return modelAndView;
    }

    @GetMapping("/contests/{code}/problems")
    public ModelAndView contestProblems(Principal principal, @PathVariable String code){
        ModelAndView modelAndView = new ModelAndView("contest/contest.html");
        String username = null;
        if(principal != null){
            username = principal.getName();
        }
        ContestDto contestDto = contestService.getContestByCode(new UserBean(username), code, false);
        List<ContestProblemDto> contestProblemDtoList = contestService.getContestProblems(new UserBean(username), contestDto.getId());
        contestDto.setProblemDtoList(contestProblemDtoList);
        modelAndView.addObject("contest", contestDto);
        return modelAndView;
    }

    @GetMapping("/contests/{code}/problems/{problem_code}")
    public ModelAndView problem(Principal principal, @PathVariable String code, @PathVariable("problem_code") String problemCode){
        ModelAndView modelAndView = new ModelAndView("contest/contest-problem.html");
        String username = null;
        if(principal != null){
            username = principal.getName();
        }
        ContestProblemDto contestProblemDto = contestService.getProblem(new UserBean(username), code, problemCode);
        if(contestProblemDto == null)
            return new ModelAndView("404.html");
        modelAndView.addObject("contest", contestProblemDto.getContestDto());
        modelAndView.addObject("problem", contestProblemDto);
        modelAndView.addObject("compilers", contestProblemDto.getProblemDto().getCompilers());

        return modelAndView;
    }
    @GetMapping("/contests/{code}/leaderboard")
    public ModelAndView leaderboard(Principal principal, @PathVariable String code, Integer page,
                                    @RequestParam(name = "sort_by", required = false, defaultValue = "score") String sort,
                                    @RequestParam(name = "sort_order", required = false, defaultValue = "desc") String order){
        ModelAndView modelAndView = new ModelAndView("contest/leaderboard.html");
        int pageSize = 20;
        Pageable pageable = createPageable(page == null ? 0 : page, sort, order, pageSize);
        String username = null;
        if(principal != null){
            username = principal.getName();
        }

        Leaderboard leaderboard = contestService.getLeaderboard(new UserBean(username), code, pageable);
        modelAndView.addObject("leaderboard", leaderboard);
        modelAndView.addObject("contest", contestService.getContestByCode(new UserBean(username), code, true));
        return modelAndView;
    }
    @GetMapping("/contests/{code}/submit/{problem_code}")
    public ModelAndView submit(Principal principal, @PathVariable String code, @PathVariable("problem_code") String problemCode){

        String username = null;
        ModelAndView modelAndView = new ModelAndView("contest/editor.html");
        if(principal != null){
            username = principal.getName();
        }
        ContestProblemDto contestProblemDto = contestService.getProblem(new UserBean(username), code, problemCode);
        modelAndView.addObject("problem", contestProblemDto.getProblemDto());
        modelAndView.addObject("contest", contestProblemDto.getContestDto());
        modelAndView.addObject("compilers", contestProblemDto.getProblemDto().getCompilers());
        return modelAndView;
    }

    @GetMapping("/contests/{code}/register")
    public ModelAndView register(Principal principal, @PathVariable String code){
        ModelAndView modelAndView = new ModelAndView("redirect:/contests/" + code);
        String username = null;
        if(principal != null){
            username = principal.getName();
            contestService.registerUser(new UserBean(username), code);
        }

        return modelAndView;
    }

    @RequestMapping(value = "/contests/{id}/submit/{contestProblemId}", method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> createSubmission(Principal principal, @PathVariable int id, @PathVariable int contestProblemId, @RequestBody MultipartFile sourceFile,
                                                                @RequestParam Integer compilerId, @RequestParam (required = false) String source){

        ResponseEntity<Map<String, String>> responseEntity;
        Map<String, String> map = new HashMap<>();
        if(principal != null){
            String username = principal.getName();
            try {
                SubmissionRequest submissionRequest = Utility.createSubmissionRequest(new UserBean(username), sourceFile, source, 0, compilerId, contestProblemId);
                submissionRequest.setContestId(id);
                int submissionId = contestService.submit(submissionRequest);
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

    @RequestMapping(value = "/contests/{code}/status/{problem}")
    public ModelAndView submissions(Principal principal, @PathVariable String code, @PathVariable String problem, Integer page,
                                    @RequestParam(name = "sort_by", required = false, defaultValue = "creationDate") String sort,
                                    @RequestParam(name = "sort_order", required = false, defaultValue = "desc") String order) {
        ModelAndView modelAndView = new ModelAndView("contest/submissions.html");
        int pageSize = 20;
        Pageable pageable = createPageable(page == null ? 0 : page, sort, order, pageSize);
        String username = null;
        if (principal != null) {
            username = principal.getName();
        }
        Page<SubmissionDto> submissions = contestService.getSubmissions(new UserBean(username), code, problem, pageable);
        modelAndView.addObject("page", submissions);
        modelAndView.addObject("contest", contestService.getContestByCode(null, code, true));
        modelAndView.addObject("problem", contestService.getProblem(null, code, problem));
        return modelAndView;
    }
}
