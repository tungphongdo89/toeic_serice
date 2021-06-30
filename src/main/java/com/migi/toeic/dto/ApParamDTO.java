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
public class ApParamDTO extends ToeicBaseDTO {

    private Long id;
    private String name;
    private String type;
    private String code;
    private String value;
    private Long status;
    private String description;
    private String parentCode;
    private List<CategoryDTO> lstLevelCode;
    private Long ord;

    public ApParam toModel()
    {
        ApParam apParam =new ApParam();
        apParam.setId(id);
        apParam.setName(name);
        apParam.setType(type);
        apParam.setCode(code);
        apParam.setValue(value);
        apParam.setStatus(status);
        apParam.setDescription(description);
        apParam.setParentCode(parentCode);
        apParam.setOrd(ord);
        return apParam;
    }
}
