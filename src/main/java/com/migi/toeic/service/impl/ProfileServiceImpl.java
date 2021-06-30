package com.migi.toeic.service.impl;

import com.migi.toeic.authen.JwtTokenUtil;
import com.migi.toeic.authen.model.RequestLogin;
import com.migi.toeic.base.common.FileUtils;
import com.migi.toeic.base.common.amazons3.UpFileAmazon;
import com.migi.toeic.dto.SysUserDTO;
import com.migi.toeic.exception.BusinessException;
import com.migi.toeic.respositories.UserRepository;
import com.migi.toeic.service.ProfileService;
import com.migi.toeic.utils.MessageUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

@Service
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailServiceImpl emailService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    Environment environment;

    @Autowired
    AuthenticationProvider authenticationProvider;

//    @Autowired
//    protected static final org.apache.log4j.Logger logger = Logger.getLogger(ProfileServiceImpl.class);


    public Boolean changePassword(String encryptedPassword, String unencryptedPassword) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        CharSequence s = unencryptedPassword;
        Boolean hashedPassword = passwordEncoder.matches(s , encryptedPassword);
        return hashedPassword;
    }

    @Override
    public SysUserDTO getDetail(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            String userName = jwtTokenUtil.getUsernameFromToken(token);     //lay username trong token
            SysUserDTO currentUser = userRepository.findByFiled("userName", userName).toModel();
//            return userRepository.getProfileDetailById(currentUser.getUserId());
            return currentUser;
        } catch (Exception e) {
            throw new BusinessException(environment.getProperty("error_getDetail"));
        }
    }

    @Override
    public String editProfile(SysUserDTO obj, HttpServletRequest request) {
        UpFileAmazon upFileAmazon = new UpFileAmazon();
        String pathFile = "";
        FileUtils fileUtils = new FileUtils();
        String token = request.getHeader("Authorization").substring(7);
        String userName = jwtTokenUtil.getUsernameFromToken(token);
        if (userRepository.findByFiled("userName", userName).toModel()==null) {
            throw new BusinessException(MessageUtils.getMessage("error_parameter"));
        } else {
            SysUserDTO currentUser = userRepository.findByFiled("userName", userName).toModel();   //tim ra user duoc update
            if(obj.getTarget()!=null) {
                currentUser.setTarget(obj.getTarget());
            }
            if(obj.getUserShowName()!= null) {
                currentUser.setUserShowName(obj.getUserShowName().trim());
            }
            if(obj.getPhone() != null){
                currentUser.setPhone(obj.getPhone());
            }
            if(obj.getTarget() == null){
                currentUser.setTarget(null);
            }
            if(obj.getFileUpload() != null){
                try{
                    pathFile = upFileAmazon.UpLoadFile(obj.getFileUpload(), null, null);
                    currentUser.setUserAvatar(pathFile);
                }
                catch (Exception e){
                    log.info("Lỗi Khi upload avatar");
                }
            }
//            LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
//            Date createDate = Date.from(localDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant());
            currentUser.setUpdateDate(new Date());
            userRepository.update(currentUser.toModel());
            return MessageUtils.getMessage("edit_profile_success");
        }
    }

    @Override
    public String resetPassword(HttpServletRequest request,  RequestLogin requestLogin) throws Exception{

        final String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                throw new BusinessException(MessageUtils.getMessage("error_get_JWT"));
            } catch (ExpiredJwtException e) {
                throw new BusinessException(MessageUtils.getMessage("error_JWT_expired"));
            } catch (SignatureException e) {
                throw new BusinessException(MessageUtils.getMessage("error_JWT_expired_incorect"));
            }
        }
        SysUserDTO userDTO = null;
        if (StringUtils.isNotBlank(username)) {
            userDTO = userRepository.findByFiled("userName", username).toModel();
        }
        if(userDTO != null) {
            if (!changePassword(userDTO.getPassword(), requestLogin.getPasswordOld())) {
                throw new BusinessException (MessageUtils.getMessage("error_password"));
            }
            if (changePassword(userDTO.getPassword(), requestLogin.getPassword())) {
                throw new BusinessException (MessageUtils.getMessage("error_passwordNew"));
            }
            userDTO.setPassword(new BCryptPasswordEncoder().encode(requestLogin.getPassword()));
            try {
                userRepository.update(userDTO.toModel());
                return MessageUtils.getMessage("reset_password_success");
            } catch (BusinessException e) {
                throw new BusinessException(MessageUtils.getMessage("reset_password_failed"));
            }
        } else {
            throw  new BusinessException(MessageUtils.getMessage("error_user_not_exist"));
        }
    }

    public boolean checkFormatEmail(String email) {
        if (email.matches("^[a-z][a-z0-9_\\.]{0,32}@[a-z0-9]{2,}(\\.[a-z0-9]{2,4}){1,2}$")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String sendEmailForgotPassword(String email) {
        // FE gửi sang BE ==== long2110%40gmail.com=
        String formatEmailBeforeCheck = email.replace("%40" , "@").replace("=" , "");
        SysUserDTO userDTO = null;
        String newPassword = "";
        if(checkFormatEmail(formatEmailBeforeCheck)){
            try {
                userDTO = userRepository.findByFiled("userName", formatEmailBeforeCheck).toModel();
                newPassword = RandomStringUtils.randomAlphabetic(7) + RandomStringUtils.randomNumeric(1) ;
                String subject = "Tạo mật khẩu mới cho người dùng quên mật khẩu";
                String message = "Mật khẩu mới của bạn là : " + newPassword;
                emailService.sendEmailMessage(formatEmailBeforeCheck , subject , message);
            } catch (Exception e) {
                throw new BusinessException(MessageUtils.getMessage("EMAIL_NOT_EXISTS"));
            }
            SysUserDTO sysUserDTO = userRepository.findByFiled("userName" , formatEmailBeforeCheck).toModel();
            sysUserDTO.setPassword(new BCryptPasswordEncoder().encode(newPassword));
            userRepository.update(sysUserDTO.toModel());
        }
        else {
            throw new BusinessException(MessageUtils.getMessage("EMAIL_NOT_FORMAT"));
        }
        return MessageUtils.getMessage("send_email_forgot_pass");
    }

    @Override
    public String settingAccouunt(SysUserDTO obj) {
        UpFileAmazon upFileAmazon = new UpFileAmazon();
        String pathFile = "";
        SysUserDTO sysUserDTO = null;
//        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
//        Date updateDate = Date.from(localDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant());
        try {
            sysUserDTO = userRepository.findByFiled("userName", obj.getUserName()).toModel();
            if (sysUserDTO.getStatus() == 1) {
                throw new BusinessException(MessageUtils.getMessage("error_account_actived_before"));
            } else if (sysUserDTO.getStatus() == 0) {
                throw new BusinessException(MessageUtils.getMessage("error_account_locked"));
            }

            sysUserDTO.setUserName(obj.getUserName());
            sysUserDTO.setUserShowName(obj.getUserShowName());
            sysUserDTO.setPassword(new BCryptPasswordEncoder().encode(obj.getPassword()));
            sysUserDTO.setStatus(1L);
            sysUserDTO.setVerified(1L);

            if(obj.getFileUpload() != null){
                try{
                    pathFile = upFileAmazon.UpLoadFile(obj.getFileUpload(), null, null);
                    sysUserDTO.setUserAvatar(pathFile);
                }
                catch (Exception e){
                    log.info("Lỗi Khi upload avatar");
                }
            }

            sysUserDTO.setUpdateDate(new Date());
            userRepository.update(sysUserDTO.toModel());
        } catch (BusinessException ex){
            throw new BusinessException(ex.getMessage());
        } catch (Exception e){
            throw new BusinessException(MessageUtils.getMessage("EMAIL_NOT_EXISTS"));
        }
        return MessageUtils.getMessage("setting_account_success");
    }
}
