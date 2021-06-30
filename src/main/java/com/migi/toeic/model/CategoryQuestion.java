//package com.migi.toeic.model;
//
//import com.migi.toeic.dto.CategoryQuestionDTO;
//import io.swagger.annotations.ApiModelProperty;
//import lombok.Data;
//
//import javax.persistence.*;
//import java.io.Serializable;
//import java.util.Date;
//
//@Data
//@Entity
//@Table(name = "category_question")
//public class CategoryQuestion implements Serializable {
//	@Id
//	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CATEGORY_QUESTION_SEQ_DB")
//	@SequenceGenerator(name = "CATEGORY_QUESTION_SEQ_DB", allocationSize = 1, sequenceName = "CATEGORY_QUESTION_SEQ")
//	@Column(name = "ID", nullable = false)
//	@ApiModelProperty(value = "id", example = "1L")
//	private Long id;
//	@Column(name = "CATEGORY_ID", nullable = false)
//	private Long categoryId;
//	@Column(name = "QUESTION_ID", nullable = false)
//	private Long questionId;
//	@Column(name = "TYPE_QUESTION")
//	private Long typeQuestion;
//	@Column(name = "STATUS")
//	private Long status;
//	@Column(name = "TYPE_FILE_1")
//	private String typeFile1;
//	@Column(name = "TYPE_FILE_2")
//	private String typeFile2;
//	@Column(name = "TYPE_FILE_3")
//	private String typeFile3;
//	@Column(name = "PATH_FILE_1")
//	private String pathFile1;
//	@Column(name = "PATH_FILE_2")
//	private String pathFile2;
//	@Column(name = "PATH_FILE_3")
//	private String pathFile3;
//	@Column(name = "TRANSCRIPT")
//	private String transcript;
//	@Column(name = "CREATED_DATE")
//	private Date createdDate;
//	@Column(name = "UPDATED_DATE")
//	private Date updatedDate;
//	@Column(name = "TARGET_SCORE")
//	private Long targetScore;
//	@Column(name = "LEVEL_CODE")
//	private String levelCode;
//	@Column(name = "SUB_CODE")
//	private String subCode;
//
//	public CategoryQuestionDTO toModel() {
//		CategoryQuestionDTO categoryQuestionDTO = new CategoryQuestionDTO();
//		categoryQuestionDTO.setId(id);
//		categoryQuestionDTO.setCategoryId(categoryId);
//		categoryQuestionDTO.setQuestionId(questionId);
//		categoryQuestionDTO.setStatus(status);
//		categoryQuestionDTO.setTypeQuestion(typeQuestion);
//		categoryQuestionDTO.setTypeFile1(typeFile1);
//		categoryQuestionDTO.setTypeFile2(typeFile2);
//		categoryQuestionDTO.setPathFile1(pathFile1);
//		categoryQuestionDTO.setPathFile2(pathFile2);
//		categoryQuestionDTO.setTranscript(transcript);
//		categoryQuestionDTO.setCreatedDate(createdDate);
//		categoryQuestionDTO.setUpdatedDate(updatedDate);
//		categoryQuestionDTO.setTargetScore(targetScore);
//		categoryQuestionDTO.setLevelCode(levelCode);
//		categoryQuestionDTO.setSubCode(subCode);
//		categoryQuestionDTO.setTypeFile3(typeFile3);
//		categoryQuestionDTO.setPathFile3(pathFile3);
//		return categoryQuestionDTO;
//	}
//}
