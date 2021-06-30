package com.migi.toeic.authen.model;

import com.migi.toeic.dto.SysUserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class JwtResponse implements Serializable {
	private static final long serialVersionUID = -8091879091924046844L;
	private final String jwttoken;
	private final SysUserDTO userDTO;

}
