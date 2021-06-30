package com.migi.toeic.service.impl;

import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.base.ResultDataDTO;
import com.migi.toeic.dto.SysUserDTO;
import com.migi.toeic.dto.UserRoleDTO;
import com.migi.toeic.exception.BusinessException;
import com.migi.toeic.model.SysUser;
import com.migi.toeic.respositories.RoleRepository;
import com.migi.toeic.respositories.UserRepository;
import com.migi.toeic.respositories.UserRolesRepository;
import com.migi.toeic.service.UserService;
import com.migi.toeic.utils.MessageUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	UserRepository userRepository;

	@Autowired
	UserRolesRepository userRolesRepository;

	@Autowired
	EmailServiceImpl emailService;

	@Autowired
	Environment environment;

	@Autowired
	RoleRepository roleRepository;

	public String changePassword(String pass) {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(pass);
		return hashedPassword;
	}

	public boolean isUserNameExist(SysUserDTO obj, boolean isCreateNew) {
		if (userRepository.isUserNameExist(obj, isCreateNew) == null) {
			return false;
		} else {
			return true;
		}
	}

	public boolean checkEmail(String email) {
		if (email.matches("^(([^<>()\\[\\]\\\\.,^%$#&*='\\/?;:\\s@!\"ÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựýỳỵỷỹ]+(\\.[^<>!()\\[\\]\\\\.,^%$#&*='\\/?;:\\s@\"ÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳýỵỷỹ]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z0-9/-_.^-]+\\.)+[a-zA-Z]{2,}))$")) {
			return true;
		} else {
			return false;
		}
	}


	@Override
	@Transactional
	public DataListDTO getListUser(SysUserDTO obj) {
		ResultDataDTO resultDto = userRepository.getUsers(obj);
		DataListDTO data = new DataListDTO();
		data.setData(resultDto.getData());
		data.setTotal(resultDto.getTotal());
		data.setSize(resultDto.getTotal());
		data.setStart(1);
		return data;
	}

	@Override
	@Transactional
	public boolean createUser(SysUserDTO obj) {
		if (obj == null) {
			throw new BusinessException(environment.getProperty("error_parameter"));
		} else if (obj.getUserName() == null || !checkEmail(obj.getUserName())) {
			throw new BusinessException(environment.getProperty("error_parameter"));
		} else if (userRepository.findByFiled("userName", obj.getUserName().toLowerCase()) != null) {
			throw new BusinessException(environment.getProperty("error_user_exist"));
		} else if (obj.getPassword() == null || obj.getPassword().equals("")) {
			throw new BusinessException(environment.getProperty("error_parameter"));
		} else {
			String hashedPassword = changePassword(obj.getPassword());
			obj.setPassword(hashedPassword);
			obj.setStatus((long) 1);
			obj.setCreateDate(new Date());
			long userId = userRepository.insert(obj.toModel());
			UserRoleDTO userRole = new UserRoleDTO();
			userRole.setRoleId((long) 1);
			userRole.setUserId(userId);
			userRole.setStatus((long) 1);
			userRolesRepository.insert(userRole.toModel());
			emailService.sendEmailMessage(obj.getUserName(), "Register Success!", "Successfully!");
			return true;
		}
	}


	@Override
	public SysUserDTO updateUser(SysUserDTO obj) {
		if (obj.getUserId() == null) {
			throw new BusinessException(MessageUtils.getMessage("ERRORR_UPDATE_USER"));
		} else if (userRepository.getUserById(obj.getUserId()) == null) {
			throw new BusinessException(MessageUtils.getMessage("ERROR_USER_NOT_EXIST"));
		} else if (isUserNameExist(obj, false)) {
			throw new BusinessException(MessageUtils.getMessage("error_userName_exist"));
		} else {
//			LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
//			Date createDate = Date.from(localDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant());
			SysUserDTO userDTO = userRepository.getUserById(obj.getUserId());
			userDTO.setUpdateDate(new Date());
			userDTO.setPaymentStatus(obj.getPaymentStatus() == null ? userDTO.getPaymentStatus() : obj.getPaymentStatus());
			userDTO.setStatus(obj.getStatus() == null ? userDTO.getStatus() : obj.getStatus());
			userDTO.setCreateDate(new Date(userDTO.getCreateDate().getTime()));
			userDTO.setNearestLoginDate(userDTO.getNearestLoginDate() != null ? new Date(userDTO.getNearestLoginDate().getTime()) : userDTO.getNearestLoginDate());
			userRepository.update(userDTO.toModel());
			return userDTO;
		}
	}

	@Override
	@Transactional
	public SysUserDTO getDetail(SysUserDTO obj) throws Exception {
		if (obj.getUserId() == null) {
			throw new BusinessException(MessageUtils.getMessage("error_parameter"));
		} else if (userRepository.getUserById(obj.getUserId()) == null) {
			throw new BusinessException(MessageUtils.getMessage("error_user_not_exist"));
		} else {
			return userRepository.getUserById(obj.getUserId());
		}
	}

	@Transactional
	public SysUserDTO findByUserProperty(String property, String value) {
		if (StringUtils.isNotBlank(property) && StringUtils.isNotBlank(value)) {
			return userRepository.findByFiled(property, value).toModel();
		}
		return null;
	}

	@Override
	public DataListDTO getListAllUser(SysUserDTO sysUserDTO) {
		DataListDTO dataListDTO = new DataListDTO();
		ResultDataDTO resultDataDTO = userRepository.getListAllUser(sysUserDTO);
		dataListDTO.setData(resultDataDTO.getData());
		;
		dataListDTO.setStart(1);
		dataListDTO.setTotal(resultDataDTO.getTotal());
		dataListDTO.setSize(resultDataDTO.getData().size());
		return dataListDTO;
	}

	@Override
	public String sendEmailInviteUser(String email, boolean checkResend) {
		String formatEmailBeforeCheck = email.replace("%40", "@").replace("=", "");
		if (checkEmail(formatEmailBeforeCheck) == false) {
			throw new BusinessException(MessageUtils.getMessage("EMAIL_NOT_FORMAT"));
		}
		try {
			SysUser userDTO = userRepository.findByFiled("userName", formatEmailBeforeCheck.toLowerCase());
			if (userDTO != null && userDTO.getStatus() != -1) {
				throw new BusinessException(MessageUtils.getMessage("EMAIL_EXISTS"));
			} else if (userDTO != null && userDTO.getStatus() == -1 && !checkResend) {
				throw new BusinessException(MessageUtils.getMessage("ACCOUNT_DONT_ACTIVE_CHECK_MAIL"));
			} else {
				String subject = "Thư mời bạn tham gia hệ thống quản lý website thi TOEIC trực tuyến";
				String message = "Nếu bạn đồng ý tham gia hãy click vào link này để thêm thông tin tài khoản : "
//                        + "http://localhost:3000/account-settings?email="
						+ "https://www.toeic.migitek.com/account-settings?email="
						+ formatEmailBeforeCheck;

				emailService.sendEmailMessage(formatEmailBeforeCheck, subject, message);
			}
		} catch (BusinessException ex) {
			throw new BusinessException(ex.getMessage());
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());

		}
		return MessageUtils.getMessage("send_email_invite_user");
	}

	@Override
	public String createUserInvited(String userName, Long roleId) {
		try {
			SysUserDTO userCheckCode = null;
			String userCode = "";
			while (true) {
				userCode = emailService.generateCodeUser();
				try {
					userCheckCode = userRepository.findByFiled("userCode", userCode).toModel();
				} catch (Exception e) {
					break;
				}
			}
			userName = userName.replace("%40", "@").replace("=", "");

			SysUserDTO sysUserDTO = new SysUserDTO();
			sysUserDTO.setUserName(userName);
			sysUserDTO.setStatus(-1L);
			sysUserDTO.setUserCode(userCode);
			sysUserDTO.setCreateDate(new Date());
			Long userId = userRepository.insert(sysUserDTO.toModel());

			UserRoleDTO userRole = new UserRoleDTO();
			userRole.setRoleId(roleId);
			userRole.setUserId(userId);
			userRole.setStatus(1L);
			userRole.setDescription("Quan tri vien");
			userRolesRepository.insert(userRole.toModel());

		} catch (Exception e) {
			throw new BusinessException(MessageUtils.getMessage("error_create_user"));
		}
		return null;
	}
}
