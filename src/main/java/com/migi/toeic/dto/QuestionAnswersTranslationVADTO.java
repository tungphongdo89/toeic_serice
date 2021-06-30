package com.migi.toeic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.migi.toeic.base.ToeicBaseDTO;
import com.migi.toeic.model.QuestionAnswerTranslationVA;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@JsonIgnoreProperties(ignoreUnknown =true)
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAnswersTranslationVADTO extends ToeicBaseDTO {
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

    public QuestionAnswerTranslationVA toModel() {
        QuestionAnswerTranslationVA questionAnswerTranslationVA = new QuestionAnswerTranslationVA();
        questionAnswerTranslationVA.setId(id);
        questionAnswerTranslationVA.setName(name);
        questionAnswerTranslationVA.setAnswer(answer);
        questionAnswerTranslationVA.setParentId(parentId);
        questionAnswerTranslationVA.setStatus(status);
        questionAnswerTranslationVA.setDescription(description);
        questionAnswerTranslationVA.setAnswersToChoose(answersToChoose);
        questionAnswerTranslationVA.setScore(score);
        return questionAnswerTranslationVA;
    }
}
