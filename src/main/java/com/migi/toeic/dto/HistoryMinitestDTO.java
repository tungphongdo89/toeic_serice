package com.migi.toeic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.migi.toeic.base.ToeicBaseDTO;
import com.migi.toeic.model.HistoryMinitest;
import com.migi.toeic.model.QuestionAnswerListening;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown =true)
@AllArgsConstructor
@NoArgsConstructor
public class HistoryMinitestDTO extends ToeicBaseDTO {

    private Long id;
    private Long testId;
    private Long userId;
    private Date doTestDate;
    private Long status;
    private String totalCorrectAnswerListening;
    private String totalCorrectAnswerReading;
    private String part1;
    private String part2;
    private String part3;
    private String part4;
    private String part5;
    private String part6;
    private String part7;
    private String part8;
    private String totalTime;
    private Date createFrom;
    private Date createTo;
    private String createFromString;
    private String createToString;
    private String createDateString;
    private String keySearch;
    private int numberTest;

    public HistoryMinitest toModel(){
        HistoryMinitest historyMinitest = new HistoryMinitest();
        historyMinitest.setId(id);
        historyMinitest.setTestId(testId);
        historyMinitest.setUserId(userId);
        historyMinitest.setDoTestDate(doTestDate);
        historyMinitest.setStatus(status);
        historyMinitest.setTotalCorrectAnswerListening(totalCorrectAnswerListening);
        historyMinitest.setTotalCorrectAnswerReading(totalCorrectAnswerReading);
        historyMinitest.setPart1(part1);
        historyMinitest.setPart2(part2);
        historyMinitest.setPart3(part3);
        historyMinitest.setPart4(part4);
        historyMinitest.setPart5(part5);
        historyMinitest.setPart6(part6);
        historyMinitest.setPart7(part7);
        historyMinitest.setPart8(part8);
        historyMinitest.setTotalTime(totalTime);
        return historyMinitest;
    }

}
