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
public class QuestionOfReadingAndComplitingDTO extends ToeicBaseDTO {
    private Long idCategory;
    private String nameCategory;
    private String typeFile1;
    private String typeFile2;
    private String pathFile1;
    private String pathFile2;
    private String transcript;
    private String levelCode;
    private Long idQuestionAnswerReading;
    private String nameQuestionAnswerReading;
    private String description;
    private String answersToChoose;
    private Float score;
    private List<QuestionAnswersDTO> listAnswerToChoose;

    private String answer;
    private Long parentId;
    private Long status;
    private Long numOfAnswer;
//    private String userChoose;
}
