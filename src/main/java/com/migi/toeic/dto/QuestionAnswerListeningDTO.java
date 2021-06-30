package com.migi.toeic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.migi.toeic.base.ToeicBaseDTO;
import com.migi.toeic.model.QuestionAnswerListening;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown =true)
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAnswerListeningDTO extends ToeicBaseDTO {
    private Long id;
    private String name;
    private String answer;
    private Long parentId;
    private Long numOfAnswer;
    private Long status;
    private String description;
    private String startTime;
    private Date endTime;
    private String answersToChoose;
    private Float score;
    private String typeFile1;
    private String typeFile2;
    private String pathFile1;
    private String pathFile2;
    private String transcript;

    private String part;

    private MultipartFile fileUpload1;
    private MultipartFile fileUpload2;
    private List<QuestionAnswersDTO> listAnswerToChoose;
    private boolean correct;
    private String userChoose;
    List<String[]> userFill;
    private String translatingQuestion;
    private String translatingQuesA;
    private String translatingQuesB;
    private String translatingQuesC;
    private String translatingQuesD;
    private Long sentenceNo;
    private String pathFileQuesA;
    private String pathFileQuesB;
    private String pathFileQuesC;
    private String pathFileQuesD;
    private String pathFileQues;
    private String nameTopic;
    private Long testId;
    private String pathFileCategory1;
    private String pathFileCategory2;

    public QuestionAnswerListening toModel() {
        QuestionAnswerListening questionAnswerListening = new QuestionAnswerListening();
        questionAnswerListening.setId(this.id);
        questionAnswerListening.setName(this.name);
        questionAnswerListening.setAnswer(this.answer);
        questionAnswerListening.setParentId(this.parentId);
        questionAnswerListening.setNumOfAnswer(this.numOfAnswer);
        questionAnswerListening.setStatus(this.status);
        questionAnswerListening.setDescription(this.description);
        questionAnswerListening.setStartTime(this.startTime);
        questionAnswerListening.setEndTime(this.endTime);
        questionAnswerListening.setAnswersToChoose(this.answersToChoose);
        questionAnswerListening.setScore(this.score);
        questionAnswerListening.setTypeFile1(this.typeFile1);
        questionAnswerListening.setTypeFile2(this.typeFile2);
        questionAnswerListening.setPartFile1(this.pathFile1);
        questionAnswerListening.setPartFile2(this.pathFile2);
        questionAnswerListening.setTranscrpit(this.transcript);
        questionAnswerListening.setTranslatingQuestion(translatingQuestion);
        questionAnswerListening.setTranslatingQuesA(translatingQuesA);
        questionAnswerListening.setTranslatingQuesB(translatingQuesB);
        questionAnswerListening.setTranslatingQuesC(translatingQuesC);
        questionAnswerListening.setTranslatingQuesD(translatingQuesD);
        questionAnswerListening.setSentenceNo(this.sentenceNo);
        questionAnswerListening.setPathFileQuesA(this.pathFileQuesA);
        questionAnswerListening.setPathFileQuesB(this.pathFileQuesB);
        questionAnswerListening.setPathFileQuesC(this.pathFileQuesC);
        questionAnswerListening.setPathFileQuesD(this.pathFileQuesD);
        questionAnswerListening.setPathFileQues(this.pathFileQues);
        questionAnswerListening.setTestId(this.testId);
        return questionAnswerListening;
    }
}
