package com.migi.toeic.model;

import com.migi.toeic.dto.DetailHistoryLisSingleDTO;
import com.migi.toeic.dto.DetailHistoryListeningDTO;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "DETAIL_HISTORY_LIS_SINGLE")
public class DetailHistoryLisSingle implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "detail_history_lis_single")
    @SequenceGenerator(sequenceName = "detail_history_lis_single_seq", name = "detail_history_lis_single", allocationSize = 1)
    private Long id;
    @Column(name = "PARENT_ID")
    private Long parentId;
    @Column(name = "USER_CHOOSE")
    private String userChoose;
    @Column(name = "CORRECT")
    private String correct;
    @Column(name = "TOPIC_NAME")
    private String topicName;
    @Column(name = "QUESTION_ID")
    private Long questionId;

    public DetailHistoryLisSingleDTO toModel(){
        DetailHistoryLisSingleDTO detailHistoryLisSingleDTO = new DetailHistoryLisSingleDTO();
        detailHistoryLisSingleDTO.setId(id);
        detailHistoryLisSingleDTO.setParentId(parentId);
        detailHistoryLisSingleDTO.setUserChoose(userChoose);
        detailHistoryLisSingleDTO.setCorrect(correct);
        detailHistoryLisSingleDTO.setTopicName(topicName);
        detailHistoryLisSingleDTO.setQuestionId(questionId);
        return detailHistoryLisSingleDTO;
    }


}
