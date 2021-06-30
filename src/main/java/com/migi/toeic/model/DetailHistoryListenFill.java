package com.migi.toeic.model;

import com.migi.toeic.dto.DetailHistoryListenFillDTO;
import com.migi.toeic.dto.HistoryPracticesDTO;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "DETAIL_HISTORY_LISTEN_FILL")
public class DetailHistoryListenFill implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Detail_History_Listen_Fill_seq_db")
    @SequenceGenerator(name = "Detail_History_Listen_Fill_seq_db",allocationSize = 1,sequenceName = "Detail_History_Listen_Fill_seq")
    private Long id;
    @Column(name = "PARENT_ID")
    private Long parentId;
    @Column(name = "QUESTION_ID")
    private Long questionId;
    @Column(name = "CATEGORY_ID")
    private Long categoryId;
    @Column(name = "USER_FILL")
    private String userFill;
    @Column(name = "INDEX_CORRECT")
    private String indexCorrect;
    @Column(name = "INDEX_INCORRECT")
    private String indexInCorrect;

    public DetailHistoryListenFillDTO toModel()
    {
        DetailHistoryListenFillDTO detailHistoryListenFillDTO =new DetailHistoryListenFillDTO();
        detailHistoryListenFillDTO.setId(id);
        detailHistoryListenFillDTO.setParentId(parentId);
        detailHistoryListenFillDTO.setQuestionId(questionId);
        detailHistoryListenFillDTO.setCategoryId(categoryId);
        detailHistoryListenFillDTO.setUserFill(userFill);
        detailHistoryListenFillDTO.setIndexCorrect(indexCorrect);
        detailHistoryListenFillDTO.setIndexInCorrect(indexInCorrect);
        return detailHistoryListenFillDTO;
    }


}
