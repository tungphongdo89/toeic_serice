package com.migi.toeic.service;

import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.dto.SysUserDTO;

public interface UserService {
	public DataListDTO getListUser(SysUserDTO obj);

	public boolean createUser(SysUserDTO obj);

	public SysUserDTO updateUser(SysUserDTO obj);

	public SysUserDTO getDetail(SysUserDTO obj) throws Exception;

	public DataListDTO getListAllUser(SysUserDTO sysUserDTO);

	public String sendEmailInviteUser(String email, boolean checkResend);

	public String createUserInvited(String userName, Long roleId);
}
