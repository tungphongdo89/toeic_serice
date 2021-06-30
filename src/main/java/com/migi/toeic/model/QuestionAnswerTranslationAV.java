package com.migi.toeic.model;

import com.migi.toeic.dto.QuestionAnswersDTO;
import com.migi.toeic.dto.QuestionAnswersTranslationAVDTO;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "Q_A_TRANSLATION_A_V")
public class QuestionAnswerTranslationAV implements Serializable {

    @Id
    @SequenceGenerator(sequenceName = "Q_A_TRANSLATION_A_V_SEQ", name = "Q_A_translation_a_v", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Q_A_translation_a_v")
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

    public QuestionAnswersTranslationAVDTO toModel() {
        QuestionAnswersTranslationAVDTO questionAnswersTranslationAVDTO = new QuestionAnswersTranslationAVDTO();
        questionAnswersTranslationAVDTO.setId(this.id);
        questionAnswersTranslationAVDTO.setName(this.name);
        questionAnswersTranslationAVDTO.setAnswer(this.answer);
        questionAnswersTranslationAVDTO.setParentId(this.parentId);
        questionAnswersTranslationAVDTO.setStatus(this.status);
        questionAnswersTranslationAVDTO.setDescription(this.description);
        questionAnswersTranslationAVDTO.setAnswersToChoose(this.answersToChoose);
        questionAnswersTranslationAVDTO.setScore(this.score);
        return questionAnswersTranslationAVDTO;
    }
}
