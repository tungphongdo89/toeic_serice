package com.migi.toeic.dto;

import com.migi.toeic.base.ToeicBaseDTO;
import com.migi.toeic.model.HistoryPractices;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApparamForGetPartOrTopicDTO extends ToeicBaseDTO {

    private String type;
    private String part;
    private String value;
    private Long status;
    private String parentCode;
}
