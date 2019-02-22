package com.dscjss.codingplatform.templates;


import com.dscjss.codingplatform.contests.exception.NotFoundException;
import com.dscjss.codingplatform.templates.dto.TemplateDto;
import com.dscjss.codingplatform.templates.model.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemplateServiceImpl implements TemplateService {


    private final TemplateRepository templateRepository;

    @Autowired
    public TemplateServiceImpl(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Override
    public TemplateDto getTemplate(int id) {
        Template template = templateRepository.getOne(id);
        if(template == null)
            throw new NotFoundException("Template not found, id :" + id + ".");
        TemplateDto templateDto = new TemplateDto();
        templateDto.setId(template.getId());
        templateDto.setBody(template.getBody());
        return templateDto;
    }

    @Override
    public TemplateDto addTemplate(TemplateDto newTemplate) {
        Template template = new Template();
        template.setBody(newTemplate.getBody());
        templateRepository.save(template);

        TemplateDto templateDto = new TemplateDto();
        templateDto.setId(template.getId());
        templateDto.setBody(template.getBody());
        return templateDto;
    }

    @Override
    public void updateTemplate(TemplateDto templateDto) {
        Template template = templateRepository.getOne(templateDto.getId());
        if(template == null)
            throw new NotFoundException("Template not found, id :" + templateDto.getId() + ".");

        template.setBody(templateDto.getBody());
        templateRepository.save(template);
    }
}
