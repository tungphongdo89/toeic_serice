package com.migi.toeic.model;

import com.migi.toeic.dto.DetailHistoryMinitestDTO;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "DETAIL_HISTORY_MINITEST")
public class DetailHistoryMinitest implements Serializable {

    @Id
    @SequenceGenerator(sequenceName = "detail_history_minitest_seq", name = "detail_history_minitest", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "detail_history_minitest")
    private Long id;
    @Column(name = "PARENT_ID")
    private Long parentId;
    @Column(name = "PART_NAME")
    private String partName;
    @Column(name = "INDEX_CORRECT_ANSWER")
    private Long indexCorrectAnswer;
    @Column(name = "INDEX_INCORRECT_ANSWER")
    private Long indexIncorrectAnswer;
    @Column(name = "QUESTION_ID")
    private Long questionId;
    @Column(name = "CATEGORY_ID")
    private Long categoryId;
    @Column(name = "STT")
    private Long stt;

    public DetailHistoryMinitestDTO toModel(){
        DetailHistoryMinitestDTO detailHistoryMinitestDTO = new DetailHistoryMinitestDTO();
        detailHistoryMinitestDTO.setId(id);
        detailHistoryMinitestDTO.setParentId(parentId);
        detailHistoryMinitestDTO.setPartName(partName);
        detailHistoryMinitestDTO.setIndexCorrectAnswer(indexCorrectAnswer);
        detailHistoryMinitestDTO.setIndexIncorrectAnswer(indexIncorrectAnswer);
        detailHistoryMinitestDTO.setQuestionId(questionId);
        detailHistoryMinitestDTO.setCategoryId(categoryId);
        detailHistoryMinitestDTO.setStt(stt);
        return detailHistoryMinitestDTO;
    }


}
