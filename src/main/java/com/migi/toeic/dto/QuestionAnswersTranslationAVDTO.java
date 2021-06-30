package com.migi.toeic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.migi.toeic.base.ToeicBaseDTO;
import com.migi.toeic.model.QuestionAnswerTranslationAV;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@JsonIgnoreProperties(ignoreUnknown =true)
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAnswersTranslationAVDTO extends ToeicBaseDTO {
    private Long id;
    private String name;
    private String answer;
    private Long parentId;
    private Long status;
    private String description;
    private String answersToChoose;
    private Float score;
    private String topicName;
    private String ap_paramName;

    public QuestionAnswerTranslationAV toModel() {
        QuestionAnswerTranslationAV questionAnswerTranslationAV = new QuestionAnswerTranslationAV();
        questionAnswerTranslationAV.setId(id);
        questionAnswerTranslationAV.setName(name);
        questionAnswerTranslationAV.setAnswer(answer);
        questionAnswerTranslationAV.setParentId(parentId);
        questionAnswerTranslationAV.setStatus(status);
        questionAnswerTranslationAV.setDescription(description);
        questionAnswerTranslationAV.setAnswersToChoose(answersToChoose);
        questionAnswerTranslationAV.setScore(score);
        return questionAnswerTranslationAV;
    }
}
