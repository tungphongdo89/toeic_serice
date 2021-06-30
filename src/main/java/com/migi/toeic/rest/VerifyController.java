package com.migi.toeic.rest;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.migi.toeic.authen.JwtTokenUtil;
import com.migi.toeic.authen.model.JwtResponse;
import com.migi.toeic.authen.model.RequestLogin;
import com.migi.toeic.constants.Constants;
import com.migi.toeic.exception.BusinessException;
import com.migi.toeic.service.impl.EmailServiceImpl;
import com.migi.toeic.service.impl.UserServiceImpl;
import com.migi.toeic.utils.MessageConfig;
import com.migi.toeic.utils.MessageUtils;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@Api(description = "verify account and user")
@JsonRootName("snapshot")
@CrossOrigin
public class VerifyController {

    @Autowired
    private EmailServiceImpl emailService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @RequestMapping(value = "/account/create", method = RequestMethod.POST)
    public ResponseEntity<?> registerAccount(@RequestBody RequestLogin requestLogin) throws Exception {

            String message = emailService.verifyAccount(requestLogin);
            return new ResponseEntity<>(message,HttpStatus.OK);
    }

    @RequestMapping(value = "/verify", method = RequestMethod.GET)
    public ResponseEntity<String> registerConfirm(@RequestParam("token") String token) throws Exception {
           String message= emailService.confirmToken(token);
           String html = "<a href='"+ MessageConfig.getMessage("path_http_redirect")+ "pages/login'>" +message+  "</a>";
           return new ResponseEntity<>(html,HttpStatus.OK);
    }
    @PostMapping(value = "/restPass")
    public  ResponseEntity<?> resetPass(@RequestBody RequestLogin requestLogin) throws Exception
    {
        emailService.sendEmailToken(requestLogin,1L);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping(value = "/verify/resetPass")
    public  ResponseEntity<?> verifyResetPass(HttpServletRequest request, @RequestBody String password)
    {
        Boolean tag= emailService.resetPass(request,password);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping(value = "/reSendEmail")
    public  ResponseEntity<?> reSendEmail(@RequestBody RequestLogin requestLogin) throws Exception
    {
        if(requestLogin.getRole().equalsIgnoreCase("ADMIN")){
            userService.sendEmailInviteUser(requestLogin.getEmail(),true);
        } else if(requestLogin.getRole().equalsIgnoreCase("STUDENT")){
            emailService.sendEmailToken(requestLogin,0L);
        }
        return new ResponseEntity<>(MessageUtils.getMessage("resend_mail_success"),HttpStatus.OK);
    }
}
