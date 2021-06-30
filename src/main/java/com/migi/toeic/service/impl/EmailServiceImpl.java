package com.migi.toeic.service.impl;

import com.migi.toeic.authen.JwtTokenUtil;
import com.migi.toeic.authen.model.RequestLogin;
import com.migi.toeic.base.CustomUserDetails;
import com.migi.toeic.dto.SysUserDTO;
import com.migi.toeic.dto.UserRoleDTO;
import com.migi.toeic.exception.BusinessException;
import com.migi.toeic.model.SysUser;
import com.migi.toeic.model.UserRole;
import com.migi.toeic.respositories.UserRepository;
import com.migi.toeic.respositories.UserRolesRepository;
import com.migi.toeic.service.UserService;
import com.migi.toeic.utils.MessageConfig;
import com.migi.toeic.utils.MessageUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Random;

@Component
public class EmailServiceImpl {

    @Autowired
    private Environment environment;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserRolesRepository userRolesRepository;


    public String generateCodeUser() {
        StringBuilder result = new StringBuilder("US");
        Random random = new Random();
        long numberLong = random.nextLong();
        result.append(String.valueOf(numberLong).substring(1, 7));
        return result.toString();
    }

    public void sendEmailMessage(String toEmail, String subject, String message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(toEmail);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        javaMailSender.send(simpleMailMessage);
    }

    public String sendEmailToken(RequestLogin requestLogin, Long tag) throws Exception {
        String infor = MessageUtils.getMessage("CLICK_TO_LINK")+"\n";
        String message = MessageConfig.getMessage("path_http_web");
        String subject = MessageUtils.getMessage("register_account_web_toeic");
        StringBuilder link = new StringBuilder(message);
        String token = "";
        UserDetails userDetails = null;
        if (tag == 0L) {
            link.append("verify?token=");
            if (StringUtils.isNotBlank(requestLogin.getEmail())) {
                SysUser sysUser = userRepository.findByFiled("userName", requestLogin.getEmail());
                if (sysUser != null) {
                    userDetails = new CustomUserDetails(sysUser.toModel(), null);
                }
            } else {
                throw new BusinessException(MessageUtils.getMessage("EMAIL_NOT_EXISTS"));
            }
            if (userDetails != null) {
                token = this.jwtTokenUtil.generateToken(userDetails);
            } else {
                throw new BusinessException(MessageUtils.getMessage("error_user_not_exist"));
            }
        } else {
            link.append("restPass?token=");
            if (StringUtils.isNotBlank(requestLogin.getEmail())) {
                SysUser sysUser = userRepository.findByFiled("userName", requestLogin.getEmail().trim());
                if (sysUser != null) {
                    userDetails = new CustomUserDetails(sysUser.toModel(), null);
                }
            } else {
                throw new BusinessException(MessageUtils.getMessage("EMAIL_NOT_EXISTS"));
            }
            if (userDetails != null) {
                token = this.jwtTokenUtil.generateToken(userDetails);
            } else {
                throw new BusinessException(MessageUtils.getMessage("EMAIL_NOT_EXISTS"));
            }
        }
        try {
            sendEmailMessage(requestLogin.getEmail(), subject, infor.concat(link.append(token).toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }

    public Long insertAccountWaitConfirm(RequestLogin requestLogin) throws Exception {
        boolean tagUserCode=true;
        String userCode="";
       do{
           String genUserCode = this.generateCodeUser();
           SysUser sysUser = userRepository.findByFiled("userCode",(String)genUserCode);
           if(sysUser != null)
           {
               tagUserCode = true;
           }
           else {
               tagUserCode =false;
               userCode = genUserCode;
           }
       }while (tagUserCode);
        SysUserDTO sysUserDTODB = null;
        if (requestLogin != null) {
            try {
                SysUser sysUser = userRepository.findByFiledIgnoreCase("userName", requestLogin.getEmail());
                if (sysUser != null) {
                    sysUserDTODB = sysUser.toModel();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //2 la account dang hoat dong, 1 la insert thanh cong
        //0 la account ngung hoat hoat, -1l account da co dang trang thai cho.
        Long tag = 1L;
        if (sysUserDTODB == null && StringUtils.isNotBlank(userCode)) {
            SysUserDTO sysUserDTO = new SysUserDTO();
            sysUserDTO.setUserName(requestLogin.getUsername());
            sysUserDTO.setPassword(new BCryptPasswordEncoder().encode(requestLogin.getPassword()));
            sysUserDTO.setUserCode(userCode);
            sysUserDTO.setStatus(-1L);
            sysUserDTO.setPaymentStatus(0L);
            sysUserDTO.setCreateDate(new Date());
            long user_id= userRepository.insert(sysUserDTO.toModel());
            UserRoleDTO userRoleDTO = new UserRoleDTO();
            userRoleDTO.setUserId(user_id);
            userRoleDTO.setRoleId(3l);
            userRoleDTO.setStatus(1L);
            userRoleDTO.setDescription("Role of account student");
            userRolesRepository.insert(userRoleDTO.toModel());
            sendEmailToken(requestLogin, 0L);
        } else if (sysUserDTODB != null && sysUserDTODB.getStatus() == -1L) {
            sendEmailToken(requestLogin, 0L);
            tag = -1L;
        } else if (sysUserDTODB != null && sysUserDTODB.getStatus() == 0L) {
            tag = 0L;
        } else {
            tag = 2L;
        }
        return tag;
    }

    public String verifyAccount(RequestLogin requestLogin) throws Exception {
        Long id = 1L;
        try {
            id = insertAccountWaitConfirm(requestLogin);
            //2 la account dang hoat dong, 1 la insert thanh cong
            //0 la account ngung hoat hoat, -1l account da co dang trang thai cho.
        } catch (BusinessException e) {

            throw new BusinessException(e.getMessage());
        }
        if (id == 2L || id == 0L) {
            throw new BusinessException(MessageUtils.getMessage("EMAIL_EXISTS"));
        } else if (id == -1L) {
            throw new BusinessException(MessageUtils.getMessage("ACCOUT_WAIT_ACTIVE"));
        } else {
            return MessageUtils.getMessage("REGISTER_SUCCESS");
        }
    }

    public String confirmToken(String token) throws Exception {
        String message ="";
        SysUserDTO sysUserDTO = null;
        UserRole userRole = null;
        String userName = null;
        if (StringUtils.isNotBlank(token)) {
            try {
                userName = jwtTokenUtil.getUsernameFromToken(token);
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                System.out.println("JWT expired");
            } catch (SignatureException e) {
                System.out.println("JWT Token has expired or incorect");
            }
        }
        if (userName != null) {
            try {
                SysUser sysUser = userRepository.findByFiled("userName", (String) userName);
                if (sysUser != null && sysUser.getStatus() != 1L) {
                    sysUserDTO = sysUser.toModel();
                } else if(sysUser != null && sysUser.getStatus() == 1L)
                {
                    message = MessageUtils.getMessage("confirm_again");
                }
            } catch (UsernameNotFoundException e) {
                throw new BusinessException(MessageUtils.getMessage("EMAIL_NOT_EXISTS"));
            }
            if(StringUtils.isNotBlank(message))
            {
               return message;
            }
            else {
                sysUserDTO.setStatus(1L);
                sysUserDTO.setVerified(1L);
                try {
                    userRepository.update(sysUserDTO.toModel());
                    message = MessageUtils.getMessage("verify_success");
                } catch (Exception e) {
                    message= MessageUtils.getMessage("verify_faild");
                    e.printStackTrace();

                }
            }

        }
        return message;
    }

    public Boolean resetPass(HttpServletRequest request, String password) {
        Boolean tag = false;
        final String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                System.out.println("JWT Token has expired");
            } catch (SignatureException e) {
                System.out.println("JWT Token has expired or incorect");
            }
        }
        SysUserDTO userDTO = null;
        if (StringUtils.isNotBlank(username)) {
            userDTO = (SysUserDTO) this.userDetailsService.loadUserByUsername(username);
        }
        if (userDTO != null && StringUtils.isNotBlank(password)) {
            userDTO.setPassword(new BCryptPasswordEncoder().encode(password));
            try {
                userService.updateUser(userDTO);
                tag = true;
            } catch (BusinessException e) {
                e.printStackTrace();
            }
        }
        return tag;
    }

    public void sendEmailMessageWithCC(String toEmail, String subject, String message,String lstCcTo[],String replyTo) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(toEmail);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        simpleMailMessage.setReplyTo(replyTo);
        simpleMailMessage.setCc(lstCcTo);
        javaMailSender.send(simpleMailMessage);
    }
}
