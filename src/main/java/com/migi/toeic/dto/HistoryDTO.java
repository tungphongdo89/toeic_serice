package com.migi.toeic.dto;

import com.migi.toeic.base.ToeicBaseDTO;
import com.migi.toeic.model.History;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryDTO extends ToeicBaseDTO {

    private Long id;
    private Long userId;
    private Date createDate;
    private Long status;
    private Long testId;
    private Long testType;
    private Long rankCode;
    private String testName;
    private Long testResult;
    private Date startTime;
    private Long targetScore;
    private Long totalScore;
    private Long listeningScore;
    private Long readingScore;
    private String userShowName;
    private String part1;
    private String part2;
    private String part3;
    private String part4;
    private String part5;
    private String part6;
    private String part7;
    private Long countCategory;
    private Date latestHomeworkTime;
    private String createFromString;
    private String createToString;
    private String createDateString;
    private Date createFrom;
    private Date createTo;
    private Long numberUser;
    private String rankOfUser;
    private String keySearch;
    private int numberTest;
    private String totalTime;
    private String pathFile1;

    public History toModel()
    {
        History history=new History();
        history.setId(this.id);
        history.setUserId(this.userId);
        history.setCreateDate(this.createDate);
        history.setStatus(this.status);
        history.setTestId(this.testId);
        history.setTotalScore(this.totalScore);
        history.setListeningScore(this.listeningScore);
        history.setReadingScore(this.readingScore);
        history.setUserShowName(this.userShowName);
        history.setPart1(this.part1);
        history.setPart2(this.part2);
        history.setPart3(this.part3);
        history.setPart4(this.part4);
        history.setPart5(this.part5);
        history.setPart6(this.part6);
        history.setPart7(this.part7);
        history.setTotalTime(this.totalTime);
        history.setTestName(this.testName);
        return history;
    }
}
