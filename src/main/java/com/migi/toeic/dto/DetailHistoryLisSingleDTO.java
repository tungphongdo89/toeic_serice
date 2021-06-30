package com.migi.toeic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.migi.toeic.base.ToeicBaseDTO;
import com.migi.toeic.model.DetailHistoryLisSingle;
import com.migi.toeic.model.DetailHistoryListening;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@JsonIgnoreProperties(ignoreUnknown =true)
@AllArgsConstructor
@NoArgsConstructor
public class DetailHistoryLisSingleDTO extends ToeicBaseDTO {

    private Long id;
    private Long parentId;
    private String userChoose;
    private String correct;
    private String topicName;
    private Long questionId;

    public DetailHistoryLisSingle toModel(){
        DetailHistoryLisSingle detailHistoryLisSingle = new DetailHistoryLisSingle();
        detailHistoryLisSingle.setId(id);
        detailHistoryLisSingle.setParentId(parentId);
        detailHistoryLisSingle.setUserChoose(userChoose);
        detailHistoryLisSingle.setCorrect(correct);
        detailHistoryLisSingle.setTopicName(topicName);
        detailHistoryLisSingle.setQuestionId(questionId);
        return detailHistoryLisSingle;
    }

}
