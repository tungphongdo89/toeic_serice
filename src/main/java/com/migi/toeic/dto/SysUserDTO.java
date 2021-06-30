package com.migi.toeic.dto;

import com.migi.toeic.base.ToeicBaseDTO;
import com.migi.toeic.model.SysUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysUserDTO extends ToeicBaseDTO {
	private Long userId;

	private String userName;

	private String password;

	private Long roleID;

	private Long status;

	private String userCode;

	private String phone;

	private String userShowName;

	private String userAvatar;

	private Date createDateTo;

	private Date createDateFrom;

	private Date createDate;

	private Date updateDate;

	private Date nearestLoginDate;

	private Date birthday;

	private Long levelCode;

	private Double paidAFee;

	private Date paymentDate;

	private Date timeExpired;

	private String userType;

	private Long typeId;

	private Long target;

	private String codeRole;

	private Long paymentStatus;

	private Long currentScore;

	private Long roleIdOfUserInvited;

	private MultipartFile fileUpload;

	private List<RoleDTO> lstRole;

	private List<ObjectDTO> lstObject;

	private Long verified;

	private Long totalScore;

	private String testName;

	private String part1;
	private String part2;
	private String part3;
	private String part4;
	private String part5;
	private String part6;
	private String part7;

	public SysUser toModel() {
		SysUser user = new SysUser();
		user.setPassword(password);
		user.setStatus(status);
		user.setUserId(userId);
		user.setUserName(userName != null ? userName.toLowerCase() : userName);
		user.setUserCode(userCode);
		user.setPhone(phone);
		user.setUserShowName(userShowName);
		user.setUserAvatar(userAvatar);
		user.setCreateDate(createDate);
		user.setUpdateDate(updateDate);
		user.setNearestLoginDate(nearestLoginDate);
		user.setBirthday(birthday);
		user.setLevelCode(levelCode);
		user.setPaidAFee(paidAFee);
		user.setPaymentDate(paymentDate);
		user.setPaymentStatus(paymentStatus);
		user.setTimeExpired(timeExpired);
		user.setTarget(target);
		user.setPaymentStatus(paymentStatus);
		user.setCurrentScore(currentScore);
		user.setPhone(phone);
		user.setVerified(verified);
		return user;
	}
}
