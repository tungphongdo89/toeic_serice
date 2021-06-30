package com.migi.toeic.authen;

import com.migi.toeic.authen.model.RequestLogin;
import com.migi.toeic.base.CustomUserDetails;
import com.migi.toeic.dto.ObjectDTO;
import com.migi.toeic.dto.RoleDTO;
import com.migi.toeic.dto.SysUserDTO;
import com.migi.toeic.exception.BusinessException;
import com.migi.toeic.service.impl.ObjectServiceImpl;
import com.migi.toeic.service.impl.RoleServiceImpl;
import com.migi.toeic.service.impl.UserDetailsServiceImpl;
import com.migi.toeic.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
	@Autowired
	UserDetailsServiceImpl userDetailServiceimlp;

	@Autowired
	UserDetailsService userDetailService;

	@Autowired
	RoleServiceImpl roleService;

	@Autowired
	ObjectServiceImpl objectService;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	JwtTokenUtil jTokenUtil;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		RequestLogin loginRequest = (RequestLogin) authentication.getPrincipal();
		CustomUserDetails customUserDetails = (CustomUserDetails) userDetailService
				.loadUserByUsername(loginRequest.getUsername());
		SysUserDTO userDTO = customUserDetails.getUser();
		// logic xac thuc user
		UsernamePasswordAuthenticationToken result = null;
		if (userDTO.getUserName().equals(loginRequest.getUsername()) && passwordEncoder
				.matches(authentication.getCredentials().toString(), customUserDetails.getPassword())) {
			List<RoleDTO> lstRole = roleService.getRoleByUserId(userDTO.getUserId());
			userDTO.setLstRole(lstRole);
			List<ObjectDTO> lstObject = objectService.getObjectByUserId(userDTO.getUserId());
			userDTO.setLstObject(lstObject);

			customUserDetails.setUser(userDTO);
			String jwt = jTokenUtil.generateToken(customUserDetails);
			customUserDetails.setJwt(jwt);
			result = new UsernamePasswordAuthenticationToken(customUserDetails, customUserDetails.getPassword(),
					customUserDetails.getAuthorities());
		}
		else {
			throw new BusinessException(MessageUtils.getMessage("username_password_incorrect"));
		}
		return result;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setUserDetailService(UserDetailsService userDetailService) {
		this.userDetailService = userDetailService;
	}

}
