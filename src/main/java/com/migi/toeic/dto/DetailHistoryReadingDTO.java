package com.migi.toeic.dto;

import com.migi.toeic.base.ToeicBaseDTO;
import com.migi.toeic.model.DetailHistoryListenFill;
import com.migi.toeic.model.DetailHistoryReading;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailHistoryReadingDTO extends ToeicBaseDTO {

    private Long id;
    private Long parentId;
    private Long questionId;
    private Long categoryId;
    private String userChoose;
    private String indexCorrect;
    private String indexInCorrect;
    private Long numberSelected;

    public DetailHistoryReading toModel()
    {
        DetailHistoryReading detailHistoryReading=new DetailHistoryReading();
        detailHistoryReading.setId(this.id);
        detailHistoryReading.setParentId(this.parentId);
        detailHistoryReading.setQuestionId(this.questionId);
        detailHistoryReading.setCategoryId(this.categoryId);
        detailHistoryReading.setUserChoose(this.userChoose);
        detailHistoryReading.setIndexCorrect(this.indexCorrect);
        detailHistoryReading.setIndexInCorrect(this.indexInCorrect);
        detailHistoryReading.setNumberSelected(this.numberSelected);
        return detailHistoryReading;
    }
}
