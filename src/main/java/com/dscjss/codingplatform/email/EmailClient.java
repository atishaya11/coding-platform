package com.dscjss.codingplatform.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class EmailClient {

    private final Logger logger = LoggerFactory.getLogger(EmailClient.class);

    private JavaMailSender javaMailSender;


    @Autowired
    public EmailClient(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void send(Email email) {
        if (email.isHtml()) {
            try {
                sendHtmlMail(email);
            } catch (MessagingException e) {
                logger.error("Could not send email to : {} Error = {}", email.getTo(), e.getMessage());
            }
        } else {
            sendPlainTextMail(email);
        }
    }

    private void sendHtmlMail(Email email) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(email.getTo());
        helper.setReplyTo(email.getFrom());
        helper.setFrom(email.getFrom());
        helper.setSubject(email.getSubject());
        helper.setText(email.getBody(), true);
        javaMailSender.send(message);
    }

    private void sendPlainTextMail(Email email) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email.getTo());
        mailMessage.setReplyTo(email.getFrom());
        mailMessage.setFrom(email.getFrom());
        mailMessage.setSubject(email.getSubject());
        mailMessage.setText(email.getBody());
        javaMailSender.send(mailMessage);
    }
}
