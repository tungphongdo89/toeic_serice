package com.migi.toeic.rest;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.migi.toeic.authen.JwtTokenUtil;
import com.migi.toeic.authen.model.JwtResponse;
import com.migi.toeic.authen.model.RequestLogin;
import com.migi.toeic.base.CustomUserDetails;
import com.migi.toeic.constants.Constants;
import com.migi.toeic.dto.SysUserDTO;
import com.migi.toeic.exception.BusinessException;
import com.migi.toeic.respositories.UserRepository;
import com.migi.toeic.service.impl.UserDetailsServiceImpl;
import com.migi.toeic.utils.MessageUtils;
import io.jsonwebtoken.lang.Assert;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@Api(description = "login ft logout")
@JsonRootName("snapshot")
@CrossOrigin
public class JwtAuthenticationController {
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	@Autowired
	AuthenticationProvider authenticationProvider;

	@Autowired
	Environment environment;

	@Autowired
	UserRepository userRepository;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<?> login(@RequestBody RequestLogin requestLogin) throws Exception {
		System.out.println(" aaa => " + MessageUtils.getMessage("contact_success"));
		SysUserDTO currentUser = null;
		try{
			currentUser = userRepository.findByFiledIgnoreCase("userName", requestLogin.getUsername()).toModel();
		}catch (Exception e) {
			throw new BusinessException(MessageUtils.getMessage("username_not_exist"));
		}
		if(currentUser.getStatus() == -1) {
			throw new BusinessException(MessageUtils.getMessage("user_not_check_mail"));
		}
		else
		if (currentUser.getStatus() == 0) {
			throw new BusinessException(MessageUtils.getMessage("user_inactive"));
		}
		// Xác thực từ username và password.
		requestLogin.setUsername(currentUser.getUserName());
		Authentication authentication = authenticationProvider
				.authenticate(new UsernamePasswordAuthenticationToken(requestLogin, requestLogin.getPassword()));
		Assert.notNull(authentication, MessageUtils.getMessage("username_password_incorrect"));

		// Nếu thông tin hợp lệ Set thông tin authentication vào Security Context
		// SecurityContextHolder.getContext().setAuthentication(authentication);

		currentUser.setNearestLoginDate(new Date());
		userRepository.update(currentUser.toModel());

		// Trả về jwt cho người dùng.
		CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
		String jwt = customUserDetails.getJwt();
		// userServiceImpl.resetLoginFailCount(customUserDetails.getUsername());
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(Constants.HEADER_NAME, "Bearer " + jwt);
		return new ResponseEntity<>(new JwtResponse(jwt, customUserDetails.getUser()), httpHeaders, HttpStatus.OK);
	}

	// xác thực username và password
	private void authenticate(String username, String password) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}
}
