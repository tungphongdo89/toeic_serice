package com.migi.toeic.model;

import com.migi.toeic.dto.SysUserDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "USERS")
@Data
public class SysUser implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user")
	@SequenceGenerator(name = "user", sequenceName = "USER_SEQ", allocationSize = 1)
	@Column(name = "USER_ID")
	private Long userId;
	@Column(name = "USER_NAME", length = 50)
	@ApiModelProperty(value = "userName", example = "push")
	private String userName;
	@ApiModelProperty(value = "password", example = "push")
	@Column(name = "PASS_WORD", length = 200)
	private String password;
	@ApiModelProperty(value = "status", example = "1L")
	@Column(name = "STATUS")
	private Long status;
	@Column(name = "CODE_USER")
	private String userCode;
	@Column(name = "PHONE")
	private String phone;
	@Column(name = "PAYMENT_STATUS")
	private Long paymentStatus;
	@Column(name = "USER_SHOW_NAME", length = 100)
	@ApiModelProperty(value = "userShowName", example = "push")
	private String userShowName;
	@Column(name = "USER_AVATAR", length = 200)
	@ApiModelProperty(value = "userAvatar", example = "push")
	private String userAvatar;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
	@Column(name = "CREATE_DATE")
	@ApiModelProperty(value = "createDate")
	private Date createDate;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
	@Column(name = "UPDATE_DATE")
	@ApiModelProperty(value = "updateDate")
	private Date updateDate;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
	@Column(name = "NEAREST_LOGIN_DATE")
	@ApiModelProperty(value = "nearestLoginDate")
	private Date nearestLoginDate;

	@Column(name = "BIRTHDAY")
	@ApiModelProperty(value = "birthday")
	private Date birthday;

	@Column(name = "LEVEL_CODE")
	@ApiModelProperty(value = "levelCode")
	private Long levelCode;
	@Column(name = "PAID_A_FEE")
	@ApiModelProperty(value = "paidAFee")
	private Double paidAFee;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PAYMENT_DATE")
	@ApiModelProperty(value = "paymentDate")
	private Date paymentDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TIME_EXPIRED")
	@ApiModelProperty(value = "timeExpired")
	private Date timeExpired;

	@Column(name = "TARGET")
	@ApiModelProperty(value = "target")
	private Long target;
	@Column(name = "CURRENT_SCORE")
	@ApiModelProperty(value = "currentScore")
	private Long currentScore;

	@Column(name = "VERIFIED")
	@ApiModelProperty(value = "verified")
	private Long verified;


	public SysUserDTO toModel() {
		SysUserDTO userDto = new SysUserDTO();
		userDto.setPassword(password);
		userDto.setStatus(status);
		userDto.setUserId(userId);
		userDto.setUserName(userName != null ? userName.toLowerCase() : userName);
		userDto.setUserCode(userCode);
		userDto.setPhone(phone);
		userDto.setUserShowName(userShowName);
		userDto.setUserAvatar(userAvatar);
		userDto.setCreateDate(createDate);
		userDto.setUpdateDate(updateDate);
		userDto.setNearestLoginDate(nearestLoginDate);
		userDto.setBirthday(birthday);
		userDto.setLevelCode(levelCode);
		userDto.setPaymentStatus(paymentStatus);
		userDto.setPaidAFee(paidAFee);
		userDto.setPaymentDate(paymentDate);
		userDto.setTimeExpired(timeExpired);
		userDto.setTarget(target);
		userDto.setPaymentStatus(paymentStatus);
		userDto.setCurrentScore(currentScore);
		userDto.setPhone(phone);
		userDto.setVerified(verified);
		return userDto;
	}
}
