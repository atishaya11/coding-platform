package com.dscjss.codingplatform.users;


import com.dscjss.codingplatform.email.EmailService;
import com.dscjss.codingplatform.users.dto.UserDto;
import com.dscjss.codingplatform.users.exception.UsernameExistsException;
import com.dscjss.codingplatform.users.model.User;
import com.dscjss.codingplatform.users.model.VerificationToken;
import com.dscjss.codingplatform.validation.EmailExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Calendar;
import java.util.Locale;

@Controller
public class UserController {

    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final EmailService emailService;

    private final static String BAD_CREDENTIALS = "BAD_CREDENTIALS";

    @Autowired
    public UserController(UserService userService, @Qualifier("userDetailsService") UserDetailsService userDetailsService, EmailService emailService) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.emailService = emailService;
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView registration(){
        ModelAndView modelAndView = new ModelAndView("user/registration.html");
        modelAndView.addObject("user", new UserDto());
        return modelAndView;
    }


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView loginPage(Principal principal, HttpServletRequest httpServletRequest){
        ModelAndView modelAndView = new ModelAndView("user/login.html");
        if(principal != null)
            return new ModelAndView("redirect:/");

        Boolean badCredentials = (Boolean) httpServletRequest.getSession().getAttribute(BAD_CREDENTIALS);
        if(badCredentials != null && badCredentials){
            modelAndView.addObject("bad_credentials", true);
            httpServletRequest.getSession().removeAttribute(BAD_CREDENTIALS);
        }
        return modelAndView;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView registerUserAccount(@ModelAttribute("user") @Valid UserDto userDto, final BindingResult result, HttpServletRequest httpServletRequest, Errors errors) {

        User registered = new User();
        if (!result.hasErrors()) {
            try {
                registered = createUserAccount(userDto, result);
            } catch (UsernameExistsException e) {
                result.rejectValue("username", "username.error.username_taken");
            } catch (EmailExistsException e) {
                result.rejectValue("email", "email.error.in_use");
            }
        }
        if (result.hasErrors()) {
            return new ModelAndView("user/registration", "user", userDto);
        }
        else {
            try {
                String appUrl = httpServletRequest.getContextPath();
                VerificationToken verificationToken = userService.createVerificationToken(registered);
               // emailService.sendVerificationEmail(registered, verificationToken, appUrl);
            } catch (Exception e) {
                e.printStackTrace();
                return new ModelAndView("emailError", "user", userDto);
            }
            return new ModelAndView("redirect:/login", "user", userDto);
        }
    }

    private User createUserAccount(UserDto accountDto, BindingResult result) throws UsernameExistsException, EmailExistsException {
        return userService.registerNewUserAccount(accountDto);
    }

    @RequestMapping(value = "/bad-user", method = RequestMethod.GET)
    public String badUser(){
        return "bad-user.html";
    }

    @RequestMapping(value = "/registration/confirm", method = RequestMethod.GET)
    public ModelAndView confirmRegistration(Locale locale, @RequestParam("token") String token) {
        VerificationToken verificationToken = userService.getVerificationToken(token);
        ModelAndView modelAndView;
        if (verificationToken == null) {
            modelAndView = new ModelAndView("invalid-token.html");
            modelAndView.addObject("message", "invalid token");
            return modelAndView;
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            modelAndView = new ModelAndView("token-expired.html");
            modelAndView.addObject("message", "Expired token");
            modelAndView.addObject("expired", true);
            modelAndView.addObject("token", token);
            return modelAndView;
        }

        user.setEnabled(true);
        userService.saveRegisteredUser(user);
        modelAndView = new ModelAndView("redirect:/login");
        modelAndView.addObject("message", "Account verified");
        return modelAndView;
    }

    @RequestMapping(value = "/user/registration/resend/token", method = RequestMethod.GET)
    @ResponseBody
    public GenericResponse resendRegistrationToken(HttpServletRequest request, @RequestParam("token") String existingToken) {
        VerificationToken newToken = userService.generateNewVerificationToken(existingToken);

        User user = userService.getUser(newToken.getToken());

        return new GenericResponse("Resend Token");
    }

    private SimpleMailMessage constructResendVerificationTokenEmail
            (String contextPath, Locale locale, VerificationToken newToken, User user) {
        String confirmationUrl =
                contextPath + "/registration/confirm?token=" + newToken.getToken();
       // String message = messages.getMessage("message.resendToken", null, locale);
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject("Resend Registration Token");
        email.setText("Resend Token" + " rn" + confirmationUrl);
        //email.setFrom(env.getProperty("support.email"));
        email.setTo(user.getEmail());
        return email;
    }
}
