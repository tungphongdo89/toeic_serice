package com.migi.toeic.authen.model;

import com.migi.toeic.dto.QuestionAnswersDTO;
import lombok.Data;

import java.util.List;

@Data
public class RequestPractice {
    private Long typeCode;
    private String levelCode;
    private String topicName;
    private String part;
    private Long categoryId;
    private String pathFile1;
    private String pathFile2;
    private String transcript;
    private List<QuestionAnswersDTO> listQuestion;
    private Long numberQuestion;
    private Long topicId;
    private Long qid;
    private String userChoose;
    private String currentLevelCode;
    private String translatingQuestion;
    private String translatingQuesA;
    private String translatingQuesB;
    private String translatingQuesC;
    private String translatingQuesD;
    private Long correctQuesNumber;

    private String pathFileQuesA;
    private String pathFileQuesB;
    private String pathFileQuesC;
    private String pathFileQuesD;
    private String pathFileQues;
}
