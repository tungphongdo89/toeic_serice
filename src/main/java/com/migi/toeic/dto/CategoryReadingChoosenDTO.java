package com.migi.toeic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown =true)
@AllArgsConstructor
@NoArgsConstructor
public class CategoryReadingChoosenDTO {
    List<QuestionReadingChoosenDTO> listQuestionReadingChooseDTO;
    List<QuestionAnswersDTO> listQuestionAnswer;
    List<CategoryDTO> listQuestionAnswerCategory;
    Long categoryId;

}
