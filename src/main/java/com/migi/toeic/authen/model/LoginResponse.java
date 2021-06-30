package com.migi.toeic.authen.model;

import com.migi.toeic.dto.SysUserDTO;
import lombok.Data;

@Data
public class LoginResponse {
    private String jwt;
    private SysUserDTO userDTO;


}
