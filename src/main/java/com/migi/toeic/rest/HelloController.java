package com.migi.toeic.rest;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.dto.SysUserDTO;
import com.migi.toeic.model.SysUser;
import com.migi.toeic.service.UserService;
import com.migi.toeic.service.impl.UserServiceImpl;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(description = "knows how receive manage user requests")
@RequestMapping("/v1")
@JsonRootName("snapshot")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class HelloController {
	@Autowired
	UserServiceImpl userService;

	@PostMapping(value = "/listUser")
	public ResponseEntity<?> lstUser(@RequestBody SysUserDTO obj) {
		DataListDTO data = userService.getListUser(obj);
		return new ResponseEntity<>(data, HttpStatus.OK);
	}

}
