package com.migi.toeic.base.common;

import com.migi.toeic.dto.ApParamDTO;
import com.migi.toeic.dto.CategoryDTO;
import com.migi.toeic.dto.TopicDTO;
import lombok.Data;

import java.util.List;

@Data
public class DataDTO {
    List<ApParamDTO> lstTypeTopic;
    List<ApParamDTO> lstPartTopic;
    List<TopicDTO> lstNameTopic;
    List<ApParamDTO> getLevelCode;
}
