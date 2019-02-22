package com.dscjss.codingplatform.templates;

import com.dscjss.codingplatform.templates.dto.TemplateDto;
import com.dscjss.codingplatform.templates.model.Template;

public interface TemplateService {

    TemplateDto getTemplate(int id);

    TemplateDto addTemplate(TemplateDto templateDto);

    void updateTemplate(TemplateDto templateDto);
}
