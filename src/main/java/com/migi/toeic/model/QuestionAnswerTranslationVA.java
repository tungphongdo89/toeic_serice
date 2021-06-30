package com.migi.toeic.model;

import com.migi.toeic.dto.QuestionAnswersTranslationVADTO;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "Q_A_TRANSLATION_V_A")
public class QuestionAnswerTranslationVA implements Serializable {

    @Id
    @SequenceGenerator(sequenceName = "Q_A_TRANSLATION_V_A_SEQ",name = "Q_A_translation_v_a",allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "Q_A_translation_v_a")
    private Long id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "ANSWER")
    private String answer;
    @Column(name = "PARENT_ID")
    private Long parentId;
    @Column(name = "STATUS")
    private Long status;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "ANSWERS_TO_CHOOSE")
    private String answersToChoose;
    @Column(name = "SCORE")
    private Float score;

    public QuestionAnswersTranslationVADTO toModel()
    {
        QuestionAnswersTranslationVADTO questionAnswersTranslationVADTO =new QuestionAnswersTranslationVADTO();
        questionAnswersTranslationVADTO.setId(this.id);
        questionAnswersTranslationVADTO.setName(this.name);
        questionAnswersTranslationVADTO.setAnswer(this.answer);
        questionAnswersTranslationVADTO.setParentId(this.parentId);
        questionAnswersTranslationVADTO.setStatus(this.status);
        questionAnswersTranslationVADTO.setDescription(this.description);
        questionAnswersTranslationVADTO.setDescription(this.answersToChoose);
        questionAnswersTranslationVADTO.setScore(this.score);
        return questionAnswersTranslationVADTO;
    }
}
