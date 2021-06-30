package com.migi.toeic.dto;

import com.migi.toeic.model.DetailsHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailHistoryDTO {
    private Long id;
    private Long parentId;
    private Long typeTest;
    private String part;
    private Long indexCorrect;
    private Long indexIncorrect;
    private Long questionId;
    private Long categoryId;

    public DetailsHistory toModel()
    {
        DetailsHistory detailsHistory=new DetailsHistory();
        detailsHistory.setId(id);
        detailsHistory.setParentId(parentId);
        detailsHistory.setTypeTest(typeTest);
        detailsHistory.setPart(part);
        detailsHistory.setIndexCorrect(indexCorrect);
        detailsHistory.setIndexIncorrect(indexIncorrect);
        detailsHistory.setQuestionId(questionId);
        detailsHistory.setCategoryId(categoryId);
        return detailsHistory;
    }
}
