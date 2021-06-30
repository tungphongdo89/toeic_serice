package com.migi.toeic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown =true)
@AllArgsConstructor
@NoArgsConstructor
public class DataInputDTO {
    private String parentCode;
    private String part_topic_code;
    private String type_topic_code;
}
