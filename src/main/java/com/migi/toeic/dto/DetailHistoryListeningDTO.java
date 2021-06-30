package com.migi.toeic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.migi.toeic.base.ToeicBaseDTO;
import com.migi.toeic.model.DetailHistoryListening;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown =true)
@AllArgsConstructor
@NoArgsConstructor
public class DetailHistoryListeningDTO extends ToeicBaseDTO {

    private Long id;
    private Long parentId;
    private Long correctIndex;
    private Long incorrectIndex;
    private Long questionId;
    private Long categoryId;

    public DetailHistoryListening toModel(){
        DetailHistoryListening DetailHistoryListening = new DetailHistoryListening();
        DetailHistoryListening.setId(id);
        DetailHistoryListening.setParentId(parentId);
        DetailHistoryListening.setCorrectIndex(correctIndex);
        DetailHistoryListening.setIncorrectIndex(incorrectIndex);
        DetailHistoryListening.setQuestionId(questionId);
        DetailHistoryListening.setCategoryId(categoryId);
        return DetailHistoryListening;
    }

}
