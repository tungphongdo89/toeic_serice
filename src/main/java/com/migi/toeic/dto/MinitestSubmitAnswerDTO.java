package com.migi.toeic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.migi.toeic.base.ToeicBaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown =true)
@AllArgsConstructor
@NoArgsConstructor
public class MinitestSubmitAnswerDTO {
   List<QuestionListeningChoosenDTO> listQuestionListeningChoosenDTOS;
   List<QuestionReadingChoosenDTO> listQuestionReadingChoosenDTOS;
   Long userId;
   String userShowName;
   Long testId;
   String totalTime;
   Long checkDetail;
   Long minitestId;
   String testName;
}
