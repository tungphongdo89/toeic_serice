package com.migi.toeic.dto;

import com.migi.toeic.base.ToeicBaseDTO;
import com.migi.toeic.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO extends ToeicBaseDTO {

	private Long categoryId;
	private String nameCategory;
	private Long status;
	private Long parentId;
	private Long typeCode;
	private String levelCode;
	private Float timeToAnswer;
	private String typeFile1;
	private String typeFile2;
	private String pathFile1;
	private String pathFile2;
	private String transcript;
	private Date createdDate;
	private Date updatedDate;

	private String namePart;
	private String nameTopic;
	private String code;
	private String part;
	private String nameType;
	private String codeTopic;

	//Dùng để xử lí ngày tháng
	private Date createdDateFrom;
	private Date createdDateTo;
	private String createdDateFromString;
	private String createdDateToString;

	// Dùng đề nhận danh sách câu hỏi
	private List<QuestionAnswersDTO> listQuestion;

	// Dùng để nhận danh sách dữ liệu đầu vào
	private MultipartFile fileUpload1;
	private MultipartFile fileUpload2;

	// Dùng đề lấy ra loại DATA_INPUT và LEVEL_CODE trong bảng ap_param
	private String paramName;
	private String paramCode;
	private String paramType;
	private String userChoose;
	private String indexCorrect;
	private String indexInCorrect;
	private Long numberSelected;
	private Long countQuestion;
	private Long countScore;
	private Long testId;
	private String typeName;

	// Điểm hiện tại
	private Float totalSubCorrect;

	// Tổng điểm câu hỏi
	private Float totalScore;
	private String partCode;
	private String arraySentenceNo;
	private String typeFile3;
	private String pathFile3;
	private Long correctQuesNumber;

	public Category toModel() {
		Category category = new Category();
		category.setCategoryId(this.categoryId);
		category.setCategoryName(this.nameCategory);
		category.setStatus(this.status);
		category.setParentId(this.parentId);
		category.setTypeCode(this.typeCode);
		category.setLevelCode(this.levelCode);
		category.setTimeToAnswer(this.timeToAnswer);
		category.setTypeFile1(this.typeFile1);
		category.setTypeFile2(this.typeFile2);
		category.setPathFile1(this.pathFile1);
		category.setPathFile2(this.pathFile2);
		category.setTranscript(this.transcript);
		category.setCreatedDate(this.createdDate);
		category.setUpdatedDate(this.updatedDate);
		category.setPartCode(this.partCode);
		category.setArraySentenceNo(this.arraySentenceNo);
		category.setTypeFile3(this.typeFile3);
		category.setPathFile3(this.pathFile3);
		return category;
	}

}
