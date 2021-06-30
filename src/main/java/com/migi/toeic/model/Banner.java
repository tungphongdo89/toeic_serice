package com.migi.toeic.model;

import com.migi.toeic.dto.BannerDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Data
@Entity
@Table(name = "BANNERS")
public class Banner implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BANNER_SEQ_DB")
	@SequenceGenerator(name = "BANNER_SEQ_DB",allocationSize = 1,sequenceName = "BANNER_SEQ")
	@Column(name = "ID", nullable = false)
	@ApiModelProperty(value = "bannerId", example = "1L")
	private Long bannerId;
	@Column(name = "NAME", nullable = false)
	private String bannerName;
	@Column(name = "PATH_IMAGE", nullable = false)
	private String pathImg;
	@Column(name = "POST_LINK")
	private String link;
	@Column(name = "ORDER_BANNER", nullable = false)
	private Long orderBanner;
	@Column(name = "STATUS", nullable = false)
	private Long status;
	@Column(name = "DESCRIPTION")
	private String description;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_TIME", nullable = false)
	private Date createdTime;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_UPDATE", nullable = false)
	private Date lastUpdate;

	public BannerDTO toModel()
	{
		BannerDTO bannerDTO = new BannerDTO();
		bannerDTO.setBannerId(this.bannerId);
		bannerDTO.setBannerName(this.bannerName);
		bannerDTO.setPathImage(this.pathImg);
		bannerDTO.setPostLink(this.link);
		bannerDTO.setOrderBanner(this.orderBanner);
		bannerDTO.setStatus(this.status);
		bannerDTO.setDescription(this.description);
		bannerDTO.setCreatedTime(this.createdTime);
		bannerDTO.setLastUpdate(this.lastUpdate);
		return bannerDTO;
	}
}
