package com.dscjss.codingplatform.email;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class ThymeleafUtil {

    private final TemplateEngine tempTemplateEngine;

    private static TemplateEngine templateEngine;

    @Autowired
    public ThymeleafUtil(TemplateEngine tempTemplateEngine) {
        this.tempTemplateEngine = tempTemplateEngine;
    }

    @PostConstruct
    void init() {
        templateEngine = tempTemplateEngine;
    }

    public static String getProcessedHtml(Map<String, String> model, String templateName) {
        final Context context = new Context();
        if (model != null) {
            model.forEach(context::setVariable);
            return templateEngine.process(templateName, context);
        }
        return "";

    }
}