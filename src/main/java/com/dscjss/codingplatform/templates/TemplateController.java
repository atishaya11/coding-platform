package com.dscjss.codingplatform.templates;

import com.dscjss.codingplatform.templates.dto.TemplateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TemplateController {


    private final TemplateService templateService;

    @Autowired
    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }


    @GetMapping("/templates/{id}")
    public TemplateDto getTemplate(@PathVariable int id){
        return templateService.getTemplate(id);
    }
}
