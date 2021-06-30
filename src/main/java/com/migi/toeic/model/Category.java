package com.migi.toeic.model;

import com.migi.toeic.dto.CategoryDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "category")
public class Category implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CATEGORY_SEQ_DB")
	@SequenceGenerator(name = "CATEGORY_SEQ_DB", allocationSize = 1, sequenceName = "CATEGORY_SEQ")
	@Column(name = "ID", nullable = false)
	@ApiModelProperty(value = "categoryId", example = "1L")
	private Long categoryId;
	@Column(name = "NAME")
	private String categoryName;
	@Column(name = "STATUS", nullable = false)
	private Long status;
	@Column(name = "PARENT_ID", nullable = false)
	private Long parentId;
	@Column(name = "TYPE_CODE", nullable = false)
	private Long typeCode;
	@Column(name = "LEVEL_CODE")
	private String levelCode;
	@Column(name = "TIME_TO_ANSWER")
	private Float timeToAnswer;
	@Column(name = "TYPE_FILE_1")
	private String typeFile1;
	@Column(name = "TYPE_FILE_2")
	private String typeFile2;
	@Column(name = "PATH_FILE_1")
	private String pathFile1;
	@Column(name = "PATH_FILE_2")
	private String pathFile2;
	@Column(name = "TRANSCRIPT")
	private String transcript;
	@Column(name = "CREATED_DATE")
	private Date createdDate;
	@Column(name = "UPDATED_DATE")
	private Date updatedDate;
	@Column(name = "PART_CODE")
	private String partCode;
	@Column(name = "ARRAY_SENTENCE_NO")
	private String arraySentenceNo;
	@Column(name = "TYPE_FILE_3")
	private String typeFile3;
	@Column(name = "PATH_FILE_3")
	private String pathFile3;

	public CategoryDTO toModel() {
		CategoryDTO categoryDTO = new CategoryDTO();
		categoryDTO.setCategoryId(categoryId);
		categoryDTO.setNameCategory(categoryName);
		categoryDTO.setStatus(status);
		categoryDTO.setParentId(parentId);
		categoryDTO.setTypeCode(typeCode);
		categoryDTO.setLevelCode(levelCode);
		categoryDTO.setTimeToAnswer(timeToAnswer);
		categoryDTO.setTypeFile1(typeFile1);
		categoryDTO.setTypeFile2(typeFile2);
		categoryDTO.setPathFile1(pathFile1);
		categoryDTO.setPathFile2(pathFile2);
		categoryDTO.setTranscript(transcript);
		categoryDTO.setCreatedDate(createdDate);
		categoryDTO.setUpdatedDate(updatedDate);
		categoryDTO.setPartCode(this.partCode);
		categoryDTO.setArraySentenceNo(this.arraySentenceNo);
		categoryDTO.setTypeFile3(this.typeFile3);
		categoryDTO.setPathFile3(this.pathFile3);
		return categoryDTO;
	}
}


