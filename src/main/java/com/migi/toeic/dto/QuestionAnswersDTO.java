package com.migi.toeic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.migi.toeic.base.ToeicBaseDTO;
import com.migi.toeic.model.QuestionAnswerListening;
import com.migi.toeic.model.QuestionAnswerReading;
import com.migi.toeic.model.QuestionAnswerTranslationAV;
import com.migi.toeic.model.QuestionAnswerTranslationVA;
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
public class QuestionAnswersDTO extends ToeicBaseDTO {
    private Long id;
    private Long idQuestionAnswerReading;
    private String name;
    private String nameQuestionAnswerReading;
    private Long parentId;
    private Long status;
    private String description;
    private String answersToChoose;
    private String answer;
    private Long levelCode;
    private Long timeToAnswer;
    private Long numOfAnswer;
    private Date endTime;
    private String startTime;
    private Float score;
    private String typeFile1;
    private String typeFile2;
    private String pathFile1;
    private String pathFile2;
    private Long correctIndex;
    private Long incorrectIndex;
    private Long categoryId;

    private MultipartFile fileUpload1;
    private MultipartFile fileUpload2;
    private Long typeQuestion;
    private Long numberCorrect;
    private Long lstSize;
    private Long numberSelected;

    // Nhận danh sách đáp án và thời gian bắt đầu của đáp án trong câu hỏi nghe
    // Sau khi nhận sẽ cắt ghép các đáp án thành 1 chuỗi lưu vào answersToChoose
    // Sau khi nhận sẽ cắt ghép startTime các đáp án thành 1 chuỗi lưu vào answersToChoose
    private List<QuestionAnswersDTO> listAnswerToChoose;

    // Nhận danh sách đáp án đúng
    // Sau khi nhận sẽ cắt ghép các đáp án thành 1 chuỗi lưu vào answer
    private List<String> listCorrectAnswers;
    private String transcript;

    // Kiểm tra đáp án đúng
    private Boolean correct;
    private String userChoose;
    private String translatingQuestion;
    private String translatingQuesA;
    private String translatingQuesB;
    private String translatingQuesC;
    private String translatingQuesD;
     // lưu list vị trí đúng, sai phần bài nghe điền từ
    private List<Integer> lstIndexCorrect;
    private List<Integer> lstIndexInCorrect;
    private List<String[]> lstAnswerCut;
    private List<String[]> lstUserFill;

    //luu vi tri dung,sai phan bai doc
    private Long indexCorrect;
    private Long indexInCorrect;

    private String pathFileCategory1;
    private String pathFileCategory2;

    private String pathFileQuesA;
    private String pathFileQuesB;
    private String pathFileQuesC;
    private String pathFileQuesD;
    private String pathFileQues;

    public QuestionAnswerListening toModelListening() {

        QuestionAnswerListening questionAnswerListening = new QuestionAnswerListening();
        questionAnswerListening.setId(id);
        questionAnswerListening.setName(name);
        questionAnswerListening.setAnswer(answer);
        questionAnswerListening.setParentId(parentId);
        questionAnswerListening.setStatus(status);
        questionAnswerListening.setDescription(description);
        questionAnswerListening.setNumOfAnswer(numOfAnswer);
        questionAnswerListening.setStartTime(startTime);
        questionAnswerListening.setEndTime(endTime);
        questionAnswerListening.setAnswersToChoose(answersToChoose);
        questionAnswerListening.setScore(score);
        questionAnswerListening.setTypeFile1(typeFile1);
        questionAnswerListening.setTypeFile2(typeFile2);
        questionAnswerListening.setPartFile1(pathFile1);
        questionAnswerListening.setPartFile2(pathFile2);
        return questionAnswerListening;
    }

    public QuestionAnswerReading toModelReading() {
        QuestionAnswerReading questionAnswerReading = new QuestionAnswerReading();
        questionAnswerReading.setId(id);
        questionAnswerReading.setName(name);
        questionAnswerReading.setAnswer(answer);
        questionAnswerReading.setParentId(parentId);
        questionAnswerReading.setStatus(status);
        questionAnswerReading.setDescription(description);
        questionAnswerReading.setNumOfAnswer(numOfAnswer);
        questionAnswerReading.setAnswersToChoose(answersToChoose);
        questionAnswerReading.setScore(score);
        return questionAnswerReading;
    }

    public QuestionAnswerTranslationVA toModelVA() {
        QuestionAnswerTranslationVA questionAnswerTranslationVA = new QuestionAnswerTranslationVA();
        questionAnswerTranslationVA.setId(id);
        questionAnswerTranslationVA.setName(name);
        questionAnswerTranslationVA.setParentId(parentId);
        questionAnswerTranslationVA.setStatus(status);
        questionAnswerTranslationVA.setDescription(description);
        questionAnswerTranslationVA.setAnswersToChoose(answersToChoose);
        questionAnswerTranslationVA.setScore(score);
        return questionAnswerTranslationVA;
    }

    public QuestionAnswerTranslationAV toModelAV() {
        QuestionAnswerTranslationAV questionAnswerTranslationAV = new QuestionAnswerTranslationAV();
        questionAnswerTranslationAV.setId(id);
        questionAnswerTranslationAV.setName(name);
        questionAnswerTranslationAV.setParentId(parentId);
        questionAnswerTranslationAV.setStatus(status);
        questionAnswerTranslationAV.setDescription(description);
        questionAnswerTranslationAV.setAnswersToChoose(answersToChoose);
        questionAnswerTranslationAV.setScore(score);
        return questionAnswerTranslationAV;
    }
}
