package com.migi.toeic.model;

import com.migi.toeic.dto.QuestionAnswerListeningDTO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "QUESTION_ANSWER_LISTENING")
public class QuestionAnswerListening implements Serializable {

	@Id
	@SequenceGenerator(sequenceName = "QUESTION_ANSWER_LISTENING_SEQ", name = "Q_A_listening", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Q_A_listening")
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
	@Column(name = "START_TIME")
	private String startTime;
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
	@Column(name = "END_TIME")
	private Date endTime;
	@Column(name = "ANSWERS_TO_CHOOSE")
	private String answersToChoose;
	@Column(name = "SCORE")
	private Float score;
	@Column(name = "TYPE_FILE_1")
	private String typeFile1;
	@Column(name = "TYPE_FILE_2")
	private String typeFile2;
	@Column(name = "PART_FILE_1")
	private String partFile1;
	@Column(name = "PART_FILE_2")
	private String partFile2;
	@Column(name = "TRANSCRIPT")
	private String transcrpit;
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
	@Column(name = "PATH_FILE_QUES_A")
	private String pathFileQuesA;
	@Column(name = "PATH_FILE_QUES_B")
	private String pathFileQuesB;
	@Column(name = "PATH_FILE_QUES_C")
	private String pathFileQuesC;
	@Column(name = "PATH_FILE_QUES_D")
	private String pathFileQuesD;
	@Column(name = "PATH_FILE_QUES")
	private String pathFileQues;
	@Column(name = "TEST_ID")
	private Long testId;

	public QuestionAnswerListeningDTO toModel() {
		QuestionAnswerListeningDTO questionAnswersListeningDTO = new QuestionAnswerListeningDTO();
		questionAnswersListeningDTO.setId(this.id);
		questionAnswersListeningDTO.setName(this.name);
		questionAnswersListeningDTO.setAnswer(this.answer);
		questionAnswersListeningDTO.setParentId(this.parentId);
		questionAnswersListeningDTO.setNumOfAnswer(this.numOfAnswer);
		questionAnswersListeningDTO.setStatus(this.status);
		questionAnswersListeningDTO.setDescription(this.description);
		questionAnswersListeningDTO.setStartTime(this.startTime);
		questionAnswersListeningDTO.setEndTime(this.endTime);
		questionAnswersListeningDTO.setAnswersToChoose(this.answersToChoose);
		questionAnswersListeningDTO.setScore(this.score);
		questionAnswersListeningDTO.setTypeFile1(this.typeFile1);
		questionAnswersListeningDTO.setTypeFile2(this.typeFile2);
		questionAnswersListeningDTO.setPathFile1(this.partFile1);
		questionAnswersListeningDTO.setPathFile2(this.partFile2);
		questionAnswersListeningDTO.setTranscript(this.transcrpit);
		questionAnswersListeningDTO.setTranslatingQuestion(this.translatingQuestion);
		questionAnswersListeningDTO.setTranslatingQuesA(this.translatingQuesA);
		questionAnswersListeningDTO.setTranslatingQuesB(this.translatingQuesB);
		questionAnswersListeningDTO.setTranslatingQuesC(this.translatingQuesC);
		questionAnswersListeningDTO.setTranslatingQuesD(this.translatingQuesD);
		questionAnswersListeningDTO.setSentenceNo(this.sentenceNo);
		questionAnswersListeningDTO.setPathFileQuesA(this.pathFileQuesA);
		questionAnswersListeningDTO.setPathFileQuesB(this.pathFileQuesB);
		questionAnswersListeningDTO.setPathFileQuesC(this.pathFileQuesC);
		questionAnswersListeningDTO.setPathFileQuesD(this.pathFileQuesD);
		questionAnswersListeningDTO.setPathFileQues(this.pathFileQues);
		questionAnswersListeningDTO.setTestId(this.testId);
		return questionAnswersListeningDTO;
	}
}
