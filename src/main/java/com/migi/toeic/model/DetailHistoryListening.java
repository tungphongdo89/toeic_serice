package com.migi.toeic.model;

import com.migi.toeic.dto.DetailHistoryListeningDTO;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "DETAIL_HISTORY_LISTENING")
public class DetailHistoryListening implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "detail_history_listening")
    @SequenceGenerator(sequenceName = "detail_history_listening_seq", name = "detail_history_listening", allocationSize = 1)
    private Long id;
    @Column(name = "PARENT_ID")
    private Long parentId;
    @Column(name = "CORRECT_INDEX")
    private Long correctIndex;
    @Column(name = "INCORRECT_INDEX")
    private Long incorrectIndex;
    @Column(name = "QUESTION_ID")
    private Long questionId;
    @Column(name = "CATEGORY_ID")
    private Long categoryId;

    public DetailHistoryListeningDTO toModel(){
        DetailHistoryListeningDTO DetailHistoryListeningDTO = new DetailHistoryListeningDTO();
        DetailHistoryListeningDTO.setId(id);
        DetailHistoryListeningDTO.setParentId(parentId);
        DetailHistoryListeningDTO.setCorrectIndex(correctIndex);
        DetailHistoryListeningDTO.setIncorrectIndex(incorrectIndex);
        DetailHistoryListeningDTO.setQuestionId(questionId);
        DetailHistoryListeningDTO.setCategoryId(categoryId);
        return DetailHistoryListeningDTO;
    }


}
