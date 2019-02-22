package com.dscjss.codingplatform.templates;

import com.dscjss.codingplatform.templates.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateRepository extends JpaRepository<Template, Integer> {
}
