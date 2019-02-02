package com.dscjss.codingplatform.contests;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
public class ContestController {


    private ContestService contestService;

    @GetMapping("/contests")
    public ModelAndView contests(Principal principal){
        return new ModelAndView("contest/contests.html");
    }

    @GetMapping("/contest/{code}")
    public ModelAndView getContest(Principal principal, @PathVariable String code){

        if(principal != null){
            String username = principal.getName();

        }

        return new ModelAndView("401.html");
    }

}
