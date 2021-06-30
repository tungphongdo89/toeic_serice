package com.migi.toeic.model;

import com.migi.toeic.dto.DetailHistoryDTO;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "DETAILS_HISTORY")
public class DetailsHistory implements Serializable {

    @Id
    @SequenceGenerator(name = "Details_History_seq_db" ,sequenceName = "Details_History_seq",allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "Details_History_seq_db")
    private Long id;
    @Column(name = "PARENT_ID")
    private Long parentId;
    @Column(name = "TYPE_TEST")
    private Long typeTest;
    @Column(name = "PART")
    private String part;
    @Column(name = "INDEX_CORRECT")
    private Long indexCorrect;
    @Column(name = "INDEX_INCORRECT")
    private Long indexIncorrect;
    @Column(name = "QUESTION_ID")
    private Long questionId;
    @Column(name = "CATEGORY_ID")
    private Long categoryId;
    public DetailHistoryDTO toModel()
    {
        DetailHistoryDTO detailHistoryDTO=new DetailHistoryDTO();
        detailHistoryDTO.setId(this.id);
        detailHistoryDTO.setParentId(this.parentId);
        detailHistoryDTO.setTypeTest(this.typeTest);
        detailHistoryDTO.setPart(this.part);
        detailHistoryDTO.setIndexCorrect(this.indexCorrect);
        detailHistoryDTO.setIndexIncorrect(this.indexIncorrect);
        detailHistoryDTO.setQuestionId(this.questionId);
        detailHistoryDTO.setCategoryId(this.categoryId);
        return detailHistoryDTO;
    }


}
