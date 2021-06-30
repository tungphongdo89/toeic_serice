package com.migi.toeic.model;

import com.migi.toeic.dto.HistoryDTO;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "HISTORY")
public class History implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "History_seq_db")
    @SequenceGenerator(name = "History_seq_db",allocationSize = 1,sequenceName = "History_seq")
    private Long id;
    @Column(name = "USER_ID")
    private Long userId;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATE")
    private Date createDate;
    @Column(name = "STATUS")
    private Long status;
    @Column(name = "TEST_ID")
    private Long testId;
    @Column(name = "TOTAL_SCORE")
    private Long totalScore;
    @Column(name = "LISTENING_SCORE")
    private Long listeningScore;
    @Column(name = "READING_SCORE")
    private Long readingScore;
    @Column(name = "USER_SHOW_NAME")
    private String userShowName;
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
    @Column(name = "TOTAL_TIME")
    private String totalTime;
    @Column(name = "TEST_NAME")
    private String testName;

    public HistoryDTO toModel()
    {
        HistoryDTO historyDTO =new HistoryDTO();
        historyDTO.setId(id);
        historyDTO.setUserId(userId);
        historyDTO.setCreateDate(createDate);
        historyDTO.setStatus(status);
        historyDTO.setTestId(testId);
        historyDTO.setTotalScore(totalScore);
        historyDTO.setUserShowName(userShowName);
        historyDTO.setListeningScore(listeningScore);
        historyDTO.setReadingScore(readingScore);
        historyDTO.setPart1(part1);
        historyDTO.setPart2(part2);
        historyDTO.setPart3(part3);
        historyDTO.setPart4(part4);
        historyDTO.setPart5(part5);
        historyDTO.setPart6(part6);
        historyDTO.setPart7(part7);
        historyDTO.setTotalTime(totalTime);
        historyDTO.setTestName(testName);
        return historyDTO;
    }


}
