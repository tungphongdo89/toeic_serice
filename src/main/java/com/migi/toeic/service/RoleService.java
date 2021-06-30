package com.migi.toeic.service;

import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.dto.RoleDTO;
import com.migi.toeic.dto.SysUserDTO;

import java.util.List;

public interface RoleService {
	public List<RoleDTO> getRoleByUserId(Long userId);
}
