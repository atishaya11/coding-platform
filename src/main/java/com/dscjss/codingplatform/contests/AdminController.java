package com.dscjss.codingplatform.contests;

import com.dscjss.codingplatform.contests.dto.ContestDto;
import com.dscjss.codingplatform.contests.dto.ContestProblemDto;
import com.dscjss.codingplatform.contests.dto.ProblemsUpdateForm;
import com.dscjss.codingplatform.contests.dto.Settings;
import com.dscjss.codingplatform.error.InvalidRequestException;
import com.dscjss.codingplatform.problems.ProblemService;
import com.dscjss.codingplatform.problems.dto.ProblemDto;
import com.dscjss.codingplatform.submissions.dto.SubmissionDto;
import com.dscjss.codingplatform.users.UserService;
import com.dscjss.codingplatform.users.dto.UserBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dscjss.codingplatform.util.Utility.createPageable;

@Controller("contestAdminController")
@RequestMapping("/admin")
public class AdminController {

    private ContestService contestService;
    private UserService userService;
    private ProblemService problemService;

    @Autowired
    public AdminController(ContestService contestService, UserService userService, ProblemService problemService) {
        this.contestService = contestService;
        this.userService = userService;
        this.problemService = problemService;
    }

    @GetMapping(value = "/contests")
    public ModelAndView contests(Principal principal, HttpServletRequest httpServletRequest){

        if(principal != null){
            UserBean userBean = userService.getUserByUsername(principal.getName());
            ModelAndView modelAndView = new ModelAndView("admin/contests.html");
            modelAndView.addObject("user", userBean);
            modelAndView.addObject("contests", contestService.getContestsByAuthor(userBean));
            return modelAndView;
        }
        return new ModelAndView("redirect:/login");

    }

    @RequestMapping(value = "/contests/create", method = RequestMethod.POST)
    public ModelAndView createContest(Principal principal, @RequestParam String name){
        if(name == null)
            return new ModelAndView("redirect:/admin/");
        ContestDto contestDto = contestService.createContestFromName(new UserBean(principal.getName()), name);
        return new ModelAndView("redirect:/admin/contests/edit/" + contestDto.getId());

    }
    @RequestMapping(value = "/contests/edit/{id}", method = RequestMethod.GET)
    public ModelAndView editContest(Principal principal, @PathVariable Integer id){
        return new ModelAndView("redirect:/admin/contests/edit/" + id + "/details");
    }
    @RequestMapping(value = "/contests/edit/{id}/details", method = RequestMethod.GET)
    public ModelAndView editContestDetails(Principal principal, @PathVariable Integer id){

        if(principal != null){
            UserBean userBean = userService.getUserByUsername(principal.getName());
            ContestDto contestDto = contestService.getContestById(userBean, id, false);
            ModelAndView modelAndView = new ModelAndView("admin/edit_contest_details.html");
            modelAndView.addObject("contest", contestDto);
            return modelAndView;
        }
        return new ModelAndView("401.html");
    }

    @RequestMapping(value = "/contests/edit/{id}/details", method = RequestMethod.POST)
    public ModelAndView updateContestDetails(Principal principal, @ModelAttribute ContestDto contestDto, @PathVariable Integer id){
        if(principal != null){
            String username = principal.getName();
            contestService.updateContestDetails(new UserBean(username), contestDto, id);
            return new ModelAndView("redirect:/admin/contests/edit/"+id+"/details");
        }
        return new ModelAndView("401.html");
    }

    @RequestMapping(value = "/contests/edit/{id}/problems")
    public ModelAndView editContestProblems(Principal principal, @PathVariable Integer id){
        if(principal != null){
            UserBean userBean = userService.getUserByUsername(principal.getName());
            ContestDto contestDto = contestService.getContestById(userBean, id, false);
            ModelAndView modelAndView = new ModelAndView("admin/edit_contest_problems.html");
            List<ContestProblemDto> contestProblemDtoList = contestService.getContestProblems(userBean, contestDto.getId());
            contestDto.setProblemDtoList(contestProblemDtoList);
            modelAndView.addObject("contest", contestDto);
            modelAndView.addObject("problems", problemService.getProblemsByAuthor(userBean));
            return modelAndView;
        }
        return new ModelAndView("401.html");
    }


    @RequestMapping(value = "/contests/edit/{id}/add_problem", method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> addContestProblem(Principal principal, @PathVariable Integer id, @RequestParam Integer problemId){

        ResponseEntity<Map<String, String>> responseEntity;
        Map<String, String> map = new HashMap<>();
        if(principal != null){
            UserBean userBean = userService.getUserByUsername(principal.getName());
            boolean added = contestService.addProblem(userBean, id, problemId);
            if(added){
                responseEntity = new ResponseEntity<>(map, HttpStatus.OK);
            }else{
                responseEntity = new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
            }
        }else{
            responseEntity = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return responseEntity;
    }

    @RequestMapping(value = "/contests/edit/{id}/problems", method = RequestMethod.POST)
    public ModelAndView updateProblemDetails(Principal principal, @PathVariable Integer id, @ModelAttribute ProblemsUpdateForm problemsUpdateForm){

        if(principal != null){
            String username = principal.getName();
            try {
                contestService.updateContestProblems(new UserBean(username), id, problemsUpdateForm);
            } catch (InvalidRequestException e) {
                e.printStackTrace();
            }
            return new ModelAndView("redirect:/admin/contests/edit/"+id+"/problems");
        }

        return new ModelAndView("error/401.html", HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(value = "/contests/edit/{id}/remove_problem/{contestProblemId}", method = RequestMethod.DELETE)
    public ResponseEntity<Map<String, String>> removeContestProblem(Principal principal, @PathVariable Integer id, @PathVariable Integer contestProblemId){

        ResponseEntity<Map<String, String>> responseEntity;
        Map<String, String> map = new HashMap<>();
        if(principal != null){
            UserBean userBean = userService.getUserByUsername(principal.getName());
            boolean deleted = contestService.removeProblem(userBean, id, contestProblemId);
            if(deleted){
                responseEntity = new ResponseEntity<>(map, HttpStatus.OK);
            }else{
                responseEntity = new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
            }
        }else{
            responseEntity = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return responseEntity;
    }
    @GetMapping(value = "/contests/edit/{id}/settings")
    public ModelAndView contestSettings(Principal principal, @PathVariable Integer id, @ModelAttribute Settings settings){
        if(principal != null){
            UserBean userBean = userService.getUserByUsername(principal.getName());
            ContestDto contestDto = contestService.getContestById(userBean, id, false);
            ModelAndView modelAndView = new ModelAndView("admin/edit_contest_settings.html");
            modelAndView.addObject("contest", contestDto);
            return modelAndView;
        }
        return new ModelAndView("error/401.html");
    }
    @PostMapping(value = "/contests/edit/{id}/settings")
    public ModelAndView editContestSettings(Principal principal, @PathVariable Integer id, @ModelAttribute Settings settings){
        if(principal != null){
            UserBean userBean = userService.getUserByUsername(principal.getName());
            contestService.updateContestSettings(userBean, id, settings);
            return new ModelAndView("redirect:/admin/contests/edit/" + id + "/settings");
        }
        return new ModelAndView("error/401.html");
    }

    @RequestMapping(value = "/contests/{id}/submissions", method = RequestMethod.GET)
    public ModelAndView contestSubmissions(Principal principal, @PathVariable Integer id, Integer page,
                                           @RequestParam(name = "sort_by", required = false, defaultValue = "creationDate") String sort,
                                           @RequestParam(name = "sort_order", required = false, defaultValue = "desc") String order){

        int pageSize = 20;
        Pageable pageable = createPageable(page == null ? 0 : page, sort, order, pageSize);
        if(principal != null){
            UserBean userBean = userService.getUserByUsername(principal.getName());
            ContestDto contestDto = contestService.getContestById(userBean, id, true);
            Page<SubmissionDto> submissionDtoPage = contestService.getAllSubmissions(userBean, id, pageable);
            ModelAndView modelAndView = new ModelAndView("admin/contest_submissions.html");
            modelAndView.addObject("page", submissionDtoPage);
            modelAndView.addObject("contest", contestDto);
            return modelAndView;
        }
        return new ModelAndView("401.html");
    }

}
