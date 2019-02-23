package com.dscjss.codingplatform.problems;


import com.dscjss.codingplatform.compilers.dto.CompilerDto;
import com.dscjss.codingplatform.problems.dto.CompilersUpdateForm;
import com.dscjss.codingplatform.problems.dto.ProblemDto;
import com.dscjss.codingplatform.problems.dto.UploadTestCaseDto;
import com.dscjss.codingplatform.problems.exception.TestDataUploadException;
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

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProblemService problemService;
    private final UserService userService;

    @Autowired
    public AdminController(ProblemService problemService, UserService userService) {
        this.problemService = problemService;
        this.userService = userService;
    }


    @GetMapping(value = {"", "/", "/problems"})
    public ModelAndView adminHome(Principal principal, HttpServletRequest httpServletRequest){

        if(principal != null){
            UserBean userBean = userService.getUserByUsername(principal.getName());
            ModelAndView modelAndView = new ModelAndView("admin/problems.html");
            modelAndView.addObject("user", userBean);
            modelAndView.addObject("problems", problemService.getProblemsByAuthor(userBean));
            return modelAndView;
        }
        return new ModelAndView("redirect:/login");

    }


    @RequestMapping(value = "/problems/create", method = RequestMethod.POST)
    public ModelAndView createProblem(Principal principal, @RequestParam String name){
        if(name == null)
            return new ModelAndView("redirect:/admin/");
        ProblemDto problemDto = problemService.createProblemFromName(new UserBean(principal.getName()), name);
        return new ModelAndView("redirect:/admin/problems/edit/" + problemDto.getId());

    }

    @GetMapping(value = {"/problems/edit/{id}", "/problems/edit/{id}/"})
    public ModelAndView editProblem(@PathVariable Integer id){
        return new ModelAndView("redirect:/admin/problems/edit/" + id + "/details");
    }


    @GetMapping(value = "/problems/edit/{id}/languages")
    public ModelAndView editProblemLanguages(Principal principal, @PathVariable Integer id){

        if(principal != null){
            String username = principal.getName();
            ModelAndView modelAndView = new ModelAndView("admin/edit-problem-languages.html");
            ProblemDto problemDto = problemService.getProblemById(new UserBean(username), id, true);
            modelAndView.addObject("problem", problemDto);
            List<CompilerDto> compilerDtoList = problemService.getCompilerList(id);
            modelAndView.addObject("compilers", compilerDtoList);
            return modelAndView;
        }
        return new ModelAndView("401.html");
    }
    @PostMapping(value = "/problems/edit/{id}/languages", consumes = "application/x-www-form-urlencoded")
    public ModelAndView editProblemLanguages(Principal principal, @ModelAttribute CompilersUpdateForm compilersUpdateForm,
                                             @PathVariable Integer id){

        ModelAndView modelAndView = new ModelAndView("redirect:/admin/problems/edit/" + id + "/languages");
        problemService.updateAllowedCompilers(new UserBean(principal.getName()), compilersUpdateForm.getCompilers(), id);
        return modelAndView;
    }
    @GetMapping(value = "/problems/edit/{id}/settings")
    public ModelAndView editProblemSettings(Principal principal, @PathVariable Integer id){

        ModelAndView modelAndView = new ModelAndView("admin/edit-problem-languages.html");
        return modelAndView;
    }


    @GetMapping(value = "/problems/edit/{id}/details")
    public ModelAndView editProblemDetails(Principal principal, @PathVariable Integer id){
        if(principal != null){
            String username = principal.getName();
            ModelAndView modelAndView = new ModelAndView("admin/edit-problem-details.html");
            ProblemDto problemDto = problemService.getProblemById(new UserBean(username), id, true);
            modelAndView.addObject("problem", problemDto);
            return modelAndView;
        }

        return new ModelAndView("error.html");
    }

    @PostMapping(value = "/problems/edit/{id}/details")
    public ModelAndView updateProblemDetails(Principal principal, @ModelAttribute ProblemDto problem, @PathVariable Integer id){

        if(principal != null){
            String username = principal.getName();
            problemService.updateProblemDetails(new UserBean(username), problem, id);
            ProblemDto problemDto = problemService.getProblemById(new UserBean(username), id, false);
            ModelAndView modelAndView = new ModelAndView("redirect:/admin/problems/edit/"  + problemDto.getId() + "/details");
            modelAndView.addObject("problem", problemDto);
            return modelAndView;
        }
        return new ModelAndView("401.html");

    }

    @RequestMapping(value = "/problems/edit/{id}/test_cases", method = RequestMethod.GET)
    public ModelAndView editProblemTestCases(Principal principal, @PathVariable Integer id){
        if(principal != null){
            String username = principal.getName();
            ModelAndView modelAndView = new ModelAndView("admin/edit-problem-test-cases.html");
            ProblemDto problemDto = problemService.getProblemById(new UserBean(username), id, false);
            modelAndView.addObject("problem", problemDto);
            return modelAndView;
        }
        return new ModelAndView("error.html");
    }

    @RequestMapping(value = "/problems/edit/{id}/test_cases/upload", method = RequestMethod.POST)
    public ModelAndView uploadTestCase(Principal principal, @ModelAttribute UploadTestCaseDto testCaseDto, @PathVariable Integer id){

        if(principal != null){
            String username = principal.getName();
            ModelAndView modelAndView = new ModelAndView("redirect:/admin/problems/edit/" + id + "/test_cases");
            try {
                problemService.uploadTestCase(new UserBean(username), testCaseDto, id);
            } catch (TestDataUploadException e) {
                e.printStackTrace();
            }
            return modelAndView;
        }
        return new ModelAndView("error.html");

    }


    @GetMapping("/problems/edit/add/{id}")
    public ResponseEntity<Map<String, String>> addToPractice(Principal principal, @PathVariable int id){
        if(principal != null){
            String username = principal.getName();
            problemService.addToPractice(new UserBean(username), id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/problems/edit/remove/{id}")
    public ResponseEntity<Map<String, String>> removeFromPractice(Principal principal, @PathVariable int id){
        if(principal != null){
            String username = principal.getName();
            problemService.removeFromPractice(new UserBean(username), id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
