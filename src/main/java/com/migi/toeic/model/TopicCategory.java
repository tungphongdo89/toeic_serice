//package com.migi.toeic.model;
//
//import io.swagger.annotations.ApiModelProperty;
//import lombok.Data;
//import org.springframework.format.annotation.DateTimeFormat;
//
//import javax.persistence.*;
//import java.io.Serializable;
//import java.util.Date;
//
//@Data
//@Entity
//@Table(name = "TOPIC_CATEGORY")
//public class TopicCategory implements Serializable {
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TOPIC_CATEGORY_SEQ_DB")
//	@SequenceGenerator(name = "TOPIC_CATEGORY_SEQ_DB", allocationSize = 1, sequenceName = "TOPIC_CATEGORY_SEQ")
//	@Column(name = "ID", nullable = false)
//	@ApiModelProperty(value = "ID", example = "1L")
//	private Long id;
//	@Column(name = "TOPIC_ID", nullable = false)
//	private Long topicId;
//	@Column(name = "CATEGORY_ID", nullable = false)
//	private Long categoryId;
//	@Column(name = "STATUS")
//	private Long status;
//
//	public TopicCategoryDTO toModel() {
//		TopicCategoryDTO topicCategoryDTO = new TopicCategoryDTO();
//		topicCategoryDTO.setId(id);
//		topicCategoryDTO.setCategoryId(categoryId);
//		topicCategoryDTO.setTopicId(topicId);
//		topicCategoryDTO.setStatus(status);
//		return topicCategoryDTO;
//	}
//}
