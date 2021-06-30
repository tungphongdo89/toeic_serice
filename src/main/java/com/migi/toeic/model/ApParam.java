package com.migi.toeic.model;

import com.migi.toeic.dto.ApParamDTO;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "AP_PARAM")
public class ApParam implements Serializable {

    @Id
    @SequenceGenerator(sequenceName = "AP_PARAM_SEQ", name = "ap_Param", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ap_Param")
    private Long id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "TYPE")
    private String type;
    @Column(name = "CODE")
    private String code;
    @Column(name = "VALUE")
    private String value;
    @Column(name = "STATUS")
    private Long status;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "PARENT_CODE")
    private String parentCode;
    @Column(name = "ORD")
    private Long ord;

    public ApParamDTO toModel()
    {
        ApParamDTO apParamDTO = new ApParamDTO();
        apParamDTO.setId(this.id);
        apParamDTO.setName(this.name);
        apParamDTO.setType(this.type);
        apParamDTO.setCode(this.code);
        apParamDTO.setValue(this.value);
        apParamDTO.setStatus(this.status);
        apParamDTO.setDescription(this.description);
        apParamDTO.setParentCode(this.parentCode);
        apParamDTO.setOrd(this.ord);
        return apParamDTO;
    }
}
