package com.migi.toeic.model;

import com.migi.toeic.dto.DetailHistoryListenFillDTO;
import com.migi.toeic.dto.DetailHistoryReadingDTO;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "DETAIL_HISTORY_READING")
public class DetailHistoryReading implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Detail_History_Reading_seq_db")
    @SequenceGenerator(name = "Detail_History_Reading_seq_db",allocationSize = 1,sequenceName = "Detail_History_Reading_seq")
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

    public DetailHistoryReadingDTO toModel()
    {
        DetailHistoryReadingDTO detailHistoryReadingDTO =new DetailHistoryReadingDTO();
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
