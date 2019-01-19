package com.dscjss.codingplatform.problems;

import com.dscjss.codingplatform.compilers.dto.CompilerDto;
import com.dscjss.codingplatform.compilers.model.Compiler;
import com.dscjss.codingplatform.problems.dto.ProblemDto;
import com.dscjss.codingplatform.problems.model.Problem;
import com.dscjss.codingplatform.users.dto.UserBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;

import static com.dscjss.codingplatform.util.Utility.createPageable;

@Controller
@RequestMapping("/problems")
public class ProblemController {


    private ProblemService problemService;

    @Autowired
    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @GetMapping(value = {"", "/"})
    public ModelAndView getProblems(Principal principal, Integer page, String sort, String order){
        ModelAndView modelAndView = new ModelAndView("problem/problems.html");
        int pageSize = 20;
        Pageable pageable = createPageable(page == null ? 0 : page, sort, order, pageSize);
        Page<ProblemDto> problemDtoPage;
        if(principal != null){
            String username = principal.getName();
            problemDtoPage = problemService.getProblems(new UserBean(username), pageable);
        }else{
            problemDtoPage = problemService.getProblems(null, pageable);
        }
        modelAndView.addObject("page", problemDtoPage);
        return modelAndView;
    }

    @GetMapping(value = "/{code}")
    public ModelAndView getProblem(@PathVariable String code){
        ProblemDto problem = problemService.getProblemByCode(null, code, false);
        List<CompilerDto> compilers = problemService.getCompilerList();
        ModelAndView modelAndView = new ModelAndView("problem/problem.html");
        modelAndView.addObject("problem", problem);
        modelAndView.addObject("compilers", compilers);
        return modelAndView;
    }
}
