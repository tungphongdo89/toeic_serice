package com.migi.toeic.dto;

import com.migi.toeic.base.ToeicBaseDTO;
import com.migi.toeic.model.UserRole;
import lombok.Data;

@Data
public class UserRoleDTO extends ToeicBaseDTO {
	private Long id;
	private Long userId;
	private Long roleId;
	private Long status;
	private String description;

	public UserRole toModel() {
		UserRole userRole = new UserRole();
		userRole.setId(this.id);
		userRole.setUserId(this.userId);
		userRole.setRoleId(this.roleId);
		userRole.setStatus(this.status);
		userRole.setDescription(this.description);
		return userRole;
	}
}
