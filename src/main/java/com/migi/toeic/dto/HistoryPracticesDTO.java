package com.migi.toeic.dto;

import com.migi.toeic.base.ToeicBaseDTO;
import com.migi.toeic.model.DetailHistoryLisSingle;
import com.migi.toeic.model.HistoryPractices;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryPracticesDTO extends ToeicBaseDTO {

    private Long id;
    private Long topicId;
    private Date createDate;
    private String typeCode;
    private String part;
    private String levelCode;
    private String numberCorrect;
    private Long userId;

    private List<QuestionAnswersDTO> lstQuestionAnswer;
    private List<UserFillDTO> userFill;
    private String createDateString;
    private String userChoose;

    private String typeName;
    private String partName;
    private String topicName;

    private Date createFrom;
    private Date createTo;
    private String createFromString;
    private String createToString;
    private String keySearch;
    private int numberTest;

    private List<CategoryReadingChoosenDTO> lstCategoryReadingChoose;

    private List<DetailHistoryListeningDTO> listDetailHistoryListening;

    private List<DetailHistoryLisSingleDTO> listDetailHistoryLisSingle;


    public HistoryPractices toModel()
    {
        HistoryPractices historyPractices =new HistoryPractices();
        historyPractices.setId(this.id);
        historyPractices.setCreateDate(this.createDate);
        historyPractices.setLevelCode(this.levelCode);
        historyPractices.setNumberCorrect(this.numberCorrect);
        historyPractices.setPart(this.part);
        historyPractices.setTopicId(this.topicId);
        historyPractices.setTypeCode(this.typeCode);
        historyPractices.setUserId(this.userId);
        return historyPractices;
    }
}
