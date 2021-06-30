package com.migi.toeic.model;


import com.migi.toeic.dto.UserRoleDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "USER_ROLES")
public class UserRole implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "user_roles")
	@SequenceGenerator(name = "user_roles",sequenceName = "USER_ROLE_SEQ",allocationSize = 1)
	@Column(name = "ID", nullable = false)
	private Long id;
	@Column(name = "USER_ID", nullable = false)
	private Long userId;
	@Column(name = "ROLE_ID", nullable = false)
	private Long roleId;
	@Column(name = "STATUS", nullable = false)
	private Long status;
	@Column(name = "DESCRIPTION", nullable = false)
	private String description;

	public UserRoleDTO toModel() {
		UserRoleDTO userRoleDTO = new UserRoleDTO();
		userRoleDTO.setId(this.id);
		userRoleDTO.setUserId(this.userId);
		userRoleDTO.setRoleId(this.roleId);
		userRoleDTO.setStatus(this.status);
		userRoleDTO.setDescription(this.description);
		return userRoleDTO;
	}
}
