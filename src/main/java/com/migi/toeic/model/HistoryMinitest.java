package com.migi.toeic.model;

import com.migi.toeic.dto.HistoryMinitestDTO;
import com.migi.toeic.dto.QuestionAnswerListeningDTO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "HISTORY_MINITEST")
public class HistoryMinitest implements Serializable {

    @Id
    @SequenceGenerator(sequenceName = "history_minitest_sequense", name = "history_minitest", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,  generator = "history_minitest")
    private Long id;
    @Column(name = "TEST_ID")
    private Long testId;
    @Column(name = "USER_ID")
    private Long userId;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DO_TEST_DATE")
    private Date doTestDate;
    @Column(name = "STATUS")
    private Long status;
    @Column(name = "TOTAL_CORRECT_ANSWER_LISTENING")
    private String totalCorrectAnswerListening;
    @Column(name = "TOTAL_CORRECT_ANSWER_READING")
    private String totalCorrectAnswerReading;
    @Column(name = "PART1")
    private String part1;
    @Column(name = "PART2")
    private String part2;
    @Column(name = "PART3")
    private String part3;
    @Column(name = "PART4")
    private String part4;
    @Column(name = "PART5")
    private String part5;
    @Column(name = "PART6")
    private String part6;
    @Column(name = "PART7")
    private String part7;
    @Column(name = "PART8")
    private String part8;
    @Column(name = "TOTAL_TIME")
    private String totalTime;

    public HistoryMinitestDTO toModel(){
        HistoryMinitestDTO historyMinitestDTO = new HistoryMinitestDTO();
        historyMinitestDTO.setId(id);
        historyMinitestDTO.setTestId(testId);
        historyMinitestDTO.setUserId(userId);
        historyMinitestDTO.setDoTestDate(doTestDate);
        historyMinitestDTO.setStatus(status);
        historyMinitestDTO.setTotalCorrectAnswerListening(totalCorrectAnswerListening);
        historyMinitestDTO.setTotalCorrectAnswerReading(totalCorrectAnswerReading);
        historyMinitestDTO.setPart1(part1);
        historyMinitestDTO.setPart2(part2);
        historyMinitestDTO.setPart3(part3);
        historyMinitestDTO.setPart4(part4);
        historyMinitestDTO.setPart5(part5);
        historyMinitestDTO.setPart6(part6);
        historyMinitestDTO.setPart7(part7);
        historyMinitestDTO.setPart8(part8);
        historyMinitestDTO.setTotalTime(totalTime);
        return historyMinitestDTO;
    }


}
