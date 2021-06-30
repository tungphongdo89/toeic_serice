package com.migi.toeic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.migi.toeic.base.ToeicBaseDTO;
import com.migi.toeic.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown =true)
@AllArgsConstructor
@NoArgsConstructor
public class CategoryMinitestDTO extends ToeicBaseDTO {
    private Long categoryId;
    private String nameCategory;
    private Long parentId;
    private String pathFile1;
    private String pathFile2;
    private Long testId;
    private String testName;

    private List<QuestionMinitestDTO> listQuestionMinitestDTOS;

    public Category toModel() {
        Category category = new Category();
        category.setCategoryId(this.categoryId);
        category.setCategoryName(this.nameCategory);
        category.setParentId(this.parentId);
        category.setPathFile1(this.pathFile1);
        category.setPathFile2(this.pathFile2);
        return category;
    }

}
