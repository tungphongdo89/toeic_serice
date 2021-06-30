package com.migi.toeic.rest;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.dto.SysUserDTO;
import com.migi.toeic.exception.BusinessException;
import com.migi.toeic.service.impl.UserServiceImpl;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@Api(description = "User CRUD")
@RequestMapping("/v1")
@JsonRootName("snapshot")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UsersController {
	@Autowired
	UserServiceImpl userService;

	@Autowired
	Environment environment;


	@PostMapping(value = "/admin/users/doSearch")
	public ResponseEntity<?> doSearch(@RequestBody SysUserDTO obj) {
		DataListDTO data = userService.getListUser(obj);
		return new ResponseEntity<>(data, HttpStatus.OK);
	}

	@PostMapping(value = "/users/create")
	public ResponseEntity<?> addUser(@RequestBody SysUserDTO obj) {
		try {
			userService.createUser(obj);
			return new ResponseEntity<>(environment.getProperty("add_success"), HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<>(Collections.singletonMap("error", e.getMessage()), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/users/update")
	public ResponseEntity<?> updateUser(@RequestBody SysUserDTO obj) {
		SysUserDTO userDTO = userService.updateUser(obj);
		return new ResponseEntity<>(userDTO, HttpStatus.OK);
	}

	@PostMapping(value = "/users/getDetail")
	public ResponseEntity<?> getDetail(@RequestBody SysUserDTO obj) throws Exception {
		SysUserDTO userDetail = userService.getDetail(obj);
		return new ResponseEntity<>(userDetail, HttpStatus.OK);
	}

	@PostMapping(value = "/users/getListAllUser")
	public ResponseEntity<?> getListAllUser(@RequestBody SysUserDTO sysUserDTO) throws Exception {

		DataListDTO dataListDTO = userService.getListAllUser(sysUserDTO);
		return new ResponseEntity<>(dataListDTO, HttpStatus.OK);
	}

	@PostMapping(value = "/users/inviteUser")
	public ResponseEntity<?> inviteUser(@RequestBody String email) {
		String message = userService.sendEmailInviteUser(email, false);
		return new ResponseEntity<>(message, HttpStatus.OK);
	}

	@PostMapping(value = "/users/createUserInvited")
	public ResponseEntity<?> createUserInvited(@RequestBody SysUserDTO obj) {
		String message = userService.createUserInvited(obj.getUserName(), obj.getRoleIdOfUserInvited());
		return new ResponseEntity<>(message, HttpStatus.OK);
	}
}
