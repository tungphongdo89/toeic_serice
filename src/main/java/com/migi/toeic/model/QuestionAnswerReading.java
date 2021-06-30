package com.migi.toeic.model;

import com.migi.toeic.dto.QuestionAnswersDTO;
import com.migi.toeic.dto.QuestionAnswersReadingDTO;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "QUESTION_ANSWER_READING")
public class QuestionAnswerReading implements Serializable {

    @Id
    @SequenceGenerator(sequenceName = "QUESTION_ANSWER_READING_SEQ", name = "Q_A_reading", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Q_A_reading")
    @Column(name = "ID")
    private Long id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "ANSWER")
    private String answer;
    @Column(name = "PARENT_ID")
    private Long parentId;
    @Column(name = "NUM_OF_ANSWER")
    private Long numOfAnswer;
    @Column(name = "STATUS")
    private Long status;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "ANSWERS_TO_CHOOSE")
    private String answersToChoose;
    @Column(name = "SCORE")
    private Float score;
    @Column(name = "TRANSLATING_QUESTION")
    private String translatingQuestion;
    @Column(name = "TRANSLATING_QUES_A")
    private String translatingQuesA;
    @Column(name = "TRANSLATING_QUES_B")
    private String translatingQuesB;
    @Column(name = "TRANSLATING_QUES_C")
    private String translatingQuesC;
    @Column(name = "TRANSLATING_QUES_D")
    private String translatingQuesD;
    @Column(name = "SENTENCE_NO")
    private Long sentenceNo;
    @Column(name = "TEST_ID")
    private Long testId;

    public QuestionAnswersReadingDTO toModel() {
        QuestionAnswersReadingDTO questionAnswersReadingDTO = new QuestionAnswersReadingDTO();
        questionAnswersReadingDTO.setId(this.id);
        questionAnswersReadingDTO.setName(this.name);
        questionAnswersReadingDTO.setAnswer(this.answer);
        questionAnswersReadingDTO.setParentId(this.parentId);
        questionAnswersReadingDTO.setNumOfAnswer(this.numOfAnswer);
        questionAnswersReadingDTO.setStatus(this.status);
        questionAnswersReadingDTO.setDescription(this.description);
        questionAnswersReadingDTO.setAnswersToChoose(this.answersToChoose);
        questionAnswersReadingDTO.setScore(this.score);
        questionAnswersReadingDTO.setTranslatingQuestion(this.translatingQuestion);
        questionAnswersReadingDTO.setTranslatingQuesA(this.translatingQuesA);
        questionAnswersReadingDTO.setTranslatingQuesB(this.translatingQuesB);
        questionAnswersReadingDTO.setTranslatingQuesC(this.translatingQuesC);
        questionAnswersReadingDTO.setTranslatingQuesD(this.translatingQuesD);
        questionAnswersReadingDTO.setSentenceNo(this.sentenceNo);
        questionAnswersReadingDTO.setTestId(this.testId);
        return questionAnswersReadingDTO;
    }
}
