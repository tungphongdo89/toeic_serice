package com.migi.toeic.dto;

import com.migi.toeic.base.ToeicBaseDTO;
import com.migi.toeic.model.DetailHistoryListenFill;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailHistoryListenFillDTO extends ToeicBaseDTO {

    private Long id;
    private Long parentId;
    private Long questionId;
    private Long categoryId;
    private String userFill;
    private String indexCorrect;
    private String indexInCorrect;

    public DetailHistoryListenFill toModel()
    {
        DetailHistoryListenFill detailHistoryListenFill=new DetailHistoryListenFill();
        detailHistoryListenFill.setId(this.id);
        detailHistoryListenFill.setParentId(this.parentId);
        detailHistoryListenFill.setQuestionId(this.questionId);
        detailHistoryListenFill.setCategoryId(this.categoryId);
        detailHistoryListenFill.setUserFill(this.userFill);
        detailHistoryListenFill.setIndexCorrect(this.indexCorrect);
        detailHistoryListenFill.setIndexInCorrect(this.indexInCorrect);
        return detailHistoryListenFill;
    }
}
