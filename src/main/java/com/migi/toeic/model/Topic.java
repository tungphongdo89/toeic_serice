package com.migi.toeic.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.migi.toeic.dto.TopicDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@Entity
@Table(name = "TOPICS")
public class Topic implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TOPIC_SEQ_DB")
    @SequenceGenerator(name = "TOPIC_SEQ_DB",allocationSize = 1,sequenceName = "TOPIC_SEQ")
    @Column(name = "ID", nullable = false)
    @ApiModelProperty(value = "topicId", example = "1L")
    private Long topicId;
    @Column(name = "NAME", nullable = false)
    private String topicName;
    @Column(name = "STATUS", nullable = false)
    private Long status;
    @Column(name = "LEVEL_CODE")
    private Long levelCode;
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @Column(name = "CREATED_TIME")
    @ApiModelProperty(value = "createdTime")
    private Date createdTime;
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @Column(name = "LAST_UPDATE")
    @ApiModelProperty(value = "lastUpdate")
    private Date lastUpdate;
    @Column(name = "CODE", nullable = false)
    private String code;
    @Column(name = "PART_TOPIC_CODE")
    private String partTopicCode;
    @Column(name = "TYPE_TOPIC_CODE", nullable = false)
    private String typeTopicCode;

    public TopicDTO toModel() {
        TopicDTO topicDTO = new TopicDTO();
        topicDTO.setTopicId(topicId);
        topicDTO.setTopicName(topicName);
        topicDTO.setStatus(status);
        topicDTO.setLevelCode(levelCode);
        topicDTO.setCreatedTime(createdTime);
        topicDTO.setLastUpdate(lastUpdate);
        topicDTO.setCode(code);
        topicDTO.setPartTopicCode(partTopicCode);
        topicDTO.setTypeTopicCode(typeTopicCode);
        return topicDTO;
    }
}

