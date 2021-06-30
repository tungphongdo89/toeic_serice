package com.migi.toeic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.migi.toeic.base.ToeicBaseDTO;
import com.migi.toeic.model.DetailHistoryMinitest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown =true)
@AllArgsConstructor
@NoArgsConstructor
public class DetailHistoryMinitestDTO extends ToeicBaseDTO {

    private Long id;
    private Long parentId;
    private String partName;
    private Long indexCorrectAnswer;
    private Long indexIncorrectAnswer;
    private Long questionId;
    private Long categoryId;
    private Long stt;

    public DetailHistoryMinitest toModel(){
        DetailHistoryMinitest detailHistoryMinitest = new DetailHistoryMinitest();
        detailHistoryMinitest.setId(id);
        detailHistoryMinitest.setParentId(parentId);
        detailHistoryMinitest.setPartName(partName);
        detailHistoryMinitest.setIndexCorrectAnswer(indexCorrectAnswer);
        detailHistoryMinitest.setIndexIncorrectAnswer(indexIncorrectAnswer);
        detailHistoryMinitest.setQuestionId(questionId);
        detailHistoryMinitest.setCategoryId(categoryId);
        detailHistoryMinitest.setStt(stt);
        return detailHistoryMinitest;
    }

}
