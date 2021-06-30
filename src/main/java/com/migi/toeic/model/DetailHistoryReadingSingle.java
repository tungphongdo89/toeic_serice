package com.migi.toeic.model;

import com.migi.toeic.dto.DetailHistoryReadingDTO;
import com.migi.toeic.dto.DetailHistoryReadingSingleDTO;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "DETAIL_HISTORY_READ_SINGLE")
public class DetailHistoryReadingSingle implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "History_Read_Single_seq_db")
    @SequenceGenerator(name = "History_Read_Single_seq_db",allocationSize = 1,sequenceName = "History_Read_Single_seq")
    private Long id;
    @Column(name = "PARENT_ID")
    private Long parentId;
    @Column(name = "QUESTION_ID")
    private Long questionId;
    @Column(name = "CATEGORY_ID")
    private Long categoryId;
    @Column(name = "USER_CHOOSE")
    private String userChoose;
    @Column(name = "INDEX_CORRECT")
    private String indexCorrect;
    @Column(name = "INDEX_INCORRECT")
    private String indexInCorrect;
    @Column(name = "NUMBER_SELECTED")
    private Long numberSelected;

    public DetailHistoryReadingSingleDTO toModel()
    {
        DetailHistoryReadingSingleDTO detailHistoryReadingDTO =new DetailHistoryReadingSingleDTO();
        detailHistoryReadingDTO.setId(id);
        detailHistoryReadingDTO.setParentId(parentId);
        detailHistoryReadingDTO.setQuestionId(questionId);
        detailHistoryReadingDTO.setCategoryId(categoryId);
        detailHistoryReadingDTO.setUserChoose(userChoose);
        detailHistoryReadingDTO.setIndexCorrect(indexCorrect);
        detailHistoryReadingDTO.setIndexInCorrect(indexInCorrect);
        detailHistoryReadingDTO.setNumberSelected(numberSelected);
        return detailHistoryReadingDTO;
    }


}
