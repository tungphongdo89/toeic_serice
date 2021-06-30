package com.migi.toeic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.migi.toeic.base.ToeicBaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@JsonIgnoreProperties(ignoreUnknown =true)
@AllArgsConstructor
@NoArgsConstructor
public class QuestionMinitestDTO extends ToeicBaseDTO {
    private Long id;
    private Long stt;
    private String name;
    private String answer;
    private String startTime;
    private String answersToChoose;
    private Float score;
    private String pathFile1;
    private String pathFile2;
    private Long indexCorrectAnswer;
    private Long indexIncorrectAnswer;
    private String description;
    private String translatingQuestion;
    private String translatingQuesA;
    private String translatingQuesB;
    private String translatingQuesC;
    private String translatingQuesD;
    private Long parentId;
    private Long numberOfAnswer;
    private Long status;
    private String typeFile1;
    private String typeFile2;
    private Long sentenceNo;
    private Long testId;
    private String pathFileQuesA;
    private String pathFileQuesB;
    private String pathFileQuesC;
    private String pathFileQuesD;
    private String pathFileQues;
}
