package com.migi.toeic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.migi.toeic.base.ToeicBaseDTO;
import com.migi.toeic.model.ApParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown =true)
@AllArgsConstructor
@NoArgsConstructor
public class ApParamTestDTO extends ToeicBaseDTO {

    private Long id;
    private String typeTestName;
    private String type;
    private String typeTestCode;
    private String typeTestValue;
    private Long status;
    private String description;
    private String parentCode;
    private String partName;
    private String partCode;
    private String partValue;
    private List<ApParamTestDTO> listApp;

    public ApParam toModel()
    {
        ApParam apParam =new ApParam();
        apParam.setId(id);
        apParam.setName(typeTestName);
        apParam.setType(type);
        apParam.setCode(typeTestCode);
        apParam.setValue(typeTestValue);
        apParam.setStatus(status);
        apParam.setDescription(description);
        apParam.setParentCode(parentCode);
        return apParam;
    }
}
