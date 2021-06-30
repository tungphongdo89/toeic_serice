package com.migi.toeic.dto;

import com.migi.toeic.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {

	private Long roleId;
	private String roleName;
	private String roleCode;
	private Long status;
	private String description;

	public Role toModel() {
		Role role = new Role();
		role.setRoleId(this.roleId);
		role.setRoleName(this.roleName);
		role.setRoleName(this.roleName);
		role.setRoleCode(this.roleCode);
		role.setStatus(this.status);
		role.setDescription(this.description);
		return role;
	}

}
