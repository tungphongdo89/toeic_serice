package com.migi.toeic.model;

import com.migi.toeic.dto.RoleDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "ROLES")
public class Role implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "roles")
	@SequenceGenerator(name = "roles",sequenceName = "ROLE_SEQ",allocationSize = 1)
	@Column(name = "ID", nullable = false)
	private Long roleId;
	@Column(name = "NAME", nullable = false)
	private String roleName;
	@Column(name = "CODE", nullable = false)
	private String roleCode;
	@Column(name = "STATUS", nullable = false)
	private Long status;
	@Column(name = "DESCRIPTION")
	private String description;

	public RoleDTO toModel() {
		RoleDTO roleDTO = new RoleDTO();
		roleDTO.setRoleId(this.roleId);
		roleDTO.setRoleName(this.roleName);
		roleDTO.setRoleCode(this.roleCode);
		roleDTO.setStatus(this.status);
		roleDTO.setDescription(this.description);
		return roleDTO;
	}

}
