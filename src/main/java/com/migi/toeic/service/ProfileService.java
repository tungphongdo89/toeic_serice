package com.migi.toeic.service;

import com.migi.toeic.authen.model.RequestLogin;
import com.migi.toeic.dto.SysUserDTO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface ProfileService {
    public SysUserDTO getDetail(HttpServletRequest request);
    public String editProfile(SysUserDTO obj , HttpServletRequest request);
    public String resetPassword(HttpServletRequest request,  RequestLogin requestLogin) throws Exception;
    public String sendEmailForgotPassword(String email);
    public String settingAccouunt(SysUserDTO obj);
}
