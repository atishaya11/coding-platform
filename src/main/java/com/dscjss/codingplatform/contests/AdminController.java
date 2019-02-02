package com.dscjss.codingplatform.contests;

import com.dscjss.codingplatform.contests.dto.ContestDto;
import com.dscjss.codingplatform.contests.dto.ContestProblemDto;
import com.dscjss.codingplatform.problems.ProblemService;
import com.dscjss.codingplatform.problems.dto.ProblemDto;
import com.dscjss.codingplatform.users.UserService;
import com.dscjss.codingplatform.users.dto.UserBean;
import org.springframework.beans.factory.annotation.Autowired;
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
}
