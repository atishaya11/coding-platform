package com.dscjss.codingplatform.templates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller("templateAdminController")
@RequestMapping("/admin")
public class AdminController {


    private final TemplateService templateService;

    @Autowired
    public AdminController(TemplateService templateService) {
        this.templateService = templateService;
    }



}
