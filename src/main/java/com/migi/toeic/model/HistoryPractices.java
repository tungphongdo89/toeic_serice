package com.migi.toeic.model;

import com.migi.toeic.dto.HistoryPracticesDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "HISTORY_PRACTICES")
public class HistoryPractices implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "History_Practices_seq_db")
    @SequenceGenerator(name = "History_Practices_seq_db",allocationSize = 1,sequenceName = "History_Practices_seq")
    private Long id;
    @Column(name = "TOPIC_ID")
    private Long topicId;
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @Column(name = "CREATED_DATE")
    @ApiModelProperty(value = "createDate")
    private Date createDate;
    @Column(name = "TYPE_CODE")
    private String typeCode;
    @Column(name = "PART")
    private String part;
    @Column(name = "LEVEL_CODE")
    private String levelCode;
    @Column(name = "NUMBER_CORRECT")
    private String numberCorrect;
    @Column(name = "USER_ID")
    private Long userId;

    public HistoryPracticesDTO toModel()
    {
        HistoryPracticesDTO historyPracticesDTO =new HistoryPracticesDTO();
        historyPracticesDTO.setId(id);
        historyPracticesDTO.setCreateDate(createDate);
        historyPracticesDTO.setLevelCode(levelCode);
        historyPracticesDTO.setNumberCorrect(numberCorrect);
        historyPracticesDTO.setPart(part);
        historyPracticesDTO.setTopicId(topicId);
        historyPracticesDTO.setTypeCode(typeCode);
        historyPracticesDTO.setUserId(userId);
        return historyPracticesDTO;
    }


}
