package com.migi.toeic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.migi.toeic.base.ToeicBaseDTO;
import com.migi.toeic.model.Test;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown =true)
public class TestDTO extends ToeicBaseDTO {

    private Long id;
    private String name;
    private Long type;
    private Long rankCode;
    private Long targetScore;
    private Long timing;
    private Long status;
    private Date create_time;
    private Date updated_time;
    private Long targetScoreFrom;
    private Long targetScoreTo;
    private Date createFrom;
    private Date createTo;
    private Date latestHomeworkTime;
    private String createFromString;
    private String createToString;
    private Long countCategory;
    private String pathFile1;
    private String pathFile2;
    private String typeFile2;
    private String typeFile1;
    private MultipartFile fileUpdate1;
    private MultipartFile fileUpdate2;
    private Long totalFaild;
    private String categoryName;

    private List<CategoryDTO> lst;
    public Test toModel()
    {
        Test TEST = new Test();
        TEST.setId(id);
        TEST.setName(name);
        TEST.setType(type);
        TEST.setRankCode(rankCode);
        TEST.setTargetScore(targetScore);
        TEST.setTiming(timing);
        TEST.setStatus(status);
        TEST.setCreateTime(create_time);
        TEST.setUpdatedTime(updated_time);
        TEST.setPathFile1(pathFile1);
        TEST.setPathFile2(pathFile2);
        TEST.setTypeFile1(typeFile1);
        TEST.setTypeFile2(typeFile2);

        return TEST;
    }

}
