package com.migi.toeic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.migi.toeic.base.ToeicBaseDTO;
import com.migi.toeic.model.QuestionAnswerListening;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown =true)
@AllArgsConstructor
@NoArgsConstructor
public class MinitestDTO extends ToeicBaseDTO {
    private String partName;
    private int totalQuestion;
    private int totalCorectAnswer;
    private int sumScore;
    private String totalTime;
    List<CategoryMinitestDTO> listCategoryMinitestDTOS;
}
