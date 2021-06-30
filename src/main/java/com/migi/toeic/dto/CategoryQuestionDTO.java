//package com.migi.toeic.dto;
//
//import com.migi.toeic.base.ToeicBaseDTO;
//import com.migi.toeic.model.CategoryQuestion;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.util.Date;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class CategoryQuestionDTO extends ToeicBaseDTO {
//	private Long id;
//	private Long categoryId;
//	private Long questionId;
//	private Long typeQuestion;
//	private Long status;
//	private String typeFile1;
//	private String typeFile2;
//	private String pathFile1;
//	private String pathFile2;
//	private String transcript;
//	private Date createdDate;
//	private Date updatedDate;
//	private Long targetScore;
//	private String levelCode;
//	private String subCode;
//	private String typeFile3;
//	private String pathFile3;
//
//	public CategoryQuestion toModel() {
//		CategoryQuestion categoryQuestion = new CategoryQuestion();
//		categoryQuestion.setId(id);
//		categoryQuestion.setCategoryId(categoryId);
//		categoryQuestion.setQuestionId(questionId);
//		categoryQuestion.setStatus(status);
//		categoryQuestion.setTypeQuestion(typeQuestion);
//		categoryQuestion.setTypeFile1(typeFile1);
//		categoryQuestion.setTypeFile2(typeFile2);
//		categoryQuestion.setPathFile1(pathFile1);
//		categoryQuestion.setPathFile2(pathFile2);
//		categoryQuestion.setTranscript(transcript);
//		categoryQuestion.setCreatedDate(createdDate);
//		categoryQuestion.setUpdatedDate(updatedDate);
//		categoryQuestion.setTargetScore(targetScore);
//		categoryQuestion.setLevelCode(levelCode);
//		categoryQuestion.setSubCode(subCode);
//		categoryQuestion.setTypeFile3(typeFile3);
//		categoryQuestion.setPathFile3(pathFile3);
//		return categoryQuestion;
//	}
//}
