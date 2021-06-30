package com.migi.toeic.dto;

import com.migi.toeic.base.ToeicBaseDTO;
import com.migi.toeic.model.Banner;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BannerDTO extends ToeicBaseDTO {

	private Long bannerId;
	private String bannerName;
	private String pathImage;
	private String postLink;
	private Long orderBanner;
	private Long status;
	private String description;
	private Date createdTime;
	private Date lastUpdate;
	private Date createFrom;
	private Date createTo;
	private String createFromString;
	private String createToString;

	private MultipartFile fileUpload;

	public Banner toModel() {
		Banner banner = new Banner();
		banner.setBannerId(this.bannerId);
		banner.setBannerName(this.bannerName);
		banner.setPathImg(this.pathImage);
		banner.setLink(this.postLink);
		banner.setOrderBanner(this.orderBanner);
		banner.setStatus(this.status);
		banner.setDescription(this.description);
		banner.setCreatedTime(this.createdTime);
		banner.setLastUpdate(this.lastUpdate);
		return banner;
	}

}
