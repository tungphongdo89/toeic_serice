package com.migi.toeic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.migi.toeic.base.ToeicBaseDTO;
import com.migi.toeic.model.QuestionAnswerReading;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAnswersReadingDTO extends ToeicBaseDTO {
	private Long id;
	private String name;
	private String answer;
	private Long parentId;
	private Long status;
	private String description;
	private Long numOfAnswer;
	private String answersToChoose;
	private Float score;
	private String translatingQuestion;
	private String translatingQuesA;
	private String translatingQuesB;
	private String translatingQuesC;
	private String translatingQuesD;

	private Long numberSelected;
	private String userChoose;
	private Long sentenceNo;
	private Long testId;

	public QuestionAnswerReading toModel() {
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
		questionAnswerReading.setTranslatingQuestion(translatingQuestion);
		questionAnswerReading.setTranslatingQuesA(translatingQuesA);
		questionAnswerReading.setTranslatingQuesB(translatingQuesB);
		questionAnswerReading.setTranslatingQuesC(translatingQuesC);
		questionAnswerReading.setTranslatingQuesD(translatingQuesD);
		questionAnswerReading.setSentenceNo(this.sentenceNo);
		questionAnswerReading.setTestId(this.testId);
		return questionAnswerReading;
	}
}
