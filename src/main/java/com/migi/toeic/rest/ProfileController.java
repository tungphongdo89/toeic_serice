package com.migi.toeic.rest;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.migi.toeic.authen.model.RequestLogin;
import com.migi.toeic.dto.SysUserDTO;
import com.migi.toeic.exception.BusinessException;
import com.migi.toeic.service.impl.ProfileServiceImpl;
import com.migi.toeic.service.impl.UserServiceImpl;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@Api(description = "Profile view and edit")
@RequestMapping("/v1")
@JsonRootName("snapshot")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProfileController {
    @Autowired
    ProfileServiceImpl profileService;

    @Autowired
    UserServiceImpl userService;


    @PostMapping(value = "/account/getDetail")
    public ResponseEntity<?> getDetail(HttpServletRequest request) {
        try {
            SysUserDTO userDetail = profileService.getDetail(request);
            return new ResponseEntity<>(userDetail, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/account/update" , consumes = "multipart/form-data")
    public ResponseEntity<?> updateProfile(@ModelAttribute SysUserDTO obj , HttpServletRequest request) {
        try {
            String updateSuccess = profileService.editProfile(obj , request);
            return new ResponseEntity<>(updateSuccess, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/account/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody RequestLogin requestLogin, HttpServletRequest request) throws Exception  {
        String updateSuccess = profileService.resetPassword(request, requestLogin);
        return new ResponseEntity<>(updateSuccess, HttpStatus.OK);
    }

    @PostMapping(value = "/account/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody String email) {
        try {
            String message = profileService.sendEmailForgotPassword(email);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/account/settingAccount" , consumes = "multipart/form-data")
    public ResponseEntity<?> settingAccount(@ModelAttribute SysUserDTO obj) {
        try {
            String message = profileService.settingAccouunt(obj);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
