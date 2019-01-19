package com.dscjss.codingplatform.email;


import com.dscjss.codingplatform.users.model.User;
import com.dscjss.codingplatform.users.model.VerificationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class EmailServiceImpl implements EmailService {


    private final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private EmailClient emailClient;

    @Autowired
    public EmailServiceImpl(EmailClient emailClient) {
        this.emailClient = emailClient;
    }

    private static final String VERIFICATION_EMAIL_TEMPLATE = "mail/confirm-registration";

    @Override
    @Async
    public void sendVerificationEmail(User user, VerificationToken verificationToken, String url) {
        Email email = new Email();
        email.setTo(user.getEmail());
        email.setSubject("Registration Confirmation");

        String verificationUrl = url + "/registration/confirm?token=" + verificationToken.getToken();
        Map<String, String> map = new HashMap<>();
        map.put("user", user.getFirstName());
        map.put("url", verificationUrl);

        String body = ThymeleafUtil.getProcessedHtml(map, VERIFICATION_EMAIL_TEMPLATE);

        email.setBody(body);
        email.setHtml(true);
        emailClient.send(email);

    }
}
