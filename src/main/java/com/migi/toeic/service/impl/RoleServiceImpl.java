package com.migi.toeic.service.impl;

import com.migi.toeic.dto.RoleDTO;
import com.migi.toeic.respositories.RoleRepository;
import com.migi.toeic.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
	@Autowired
	RoleRepository roleRepository;

	@Override
	public List<RoleDTO> getRoleByUserId(Long userId) {
		List<RoleDTO> lstRole = roleRepository.getRoleByUserID(userId);
		return lstRole;
	}
}
