package com.migi.toeic.dto;
import com.migi.toeic.base.ToeicBaseDTO;
import com.migi.toeic.model.Topic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

import javax.rmi.CORBA.Tie;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicDTO extends ToeicBaseDTO {

    private Long topicId;
    private String topicName;
    private Long status;
    private Long levelCode;
    private Date createdTime;
    private Date lastUpdate;
    private String code;
    private String partTopicCode;
    private String typeTopicCode;

    private Long paramId;
    private String paramName;
    private String paramCode;
    private String paramType;
    private String paramValue;
    private String paramParentCode;
    private Long ord;

    public Topic toModel() {
        Topic topic = new Topic();
        topic.setTopicId(this.topicId);
        topic.setTopicName(this.topicName);
        topic.setStatus(this.status);
        topic.setLevelCode(this.levelCode);
        topic.setCreatedTime(this.createdTime);
        topic.setLastUpdate(this.lastUpdate);
        topic.setCode(this.code);
        topic.setPartTopicCode(this.partTopicCode);
        topic.setTypeTopicCode(this.typeTopicCode);
        return topic;
    }

}

