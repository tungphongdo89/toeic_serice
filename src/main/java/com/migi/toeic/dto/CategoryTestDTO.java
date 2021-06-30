package com.migi.toeic.dto;

import com.migi.toeic.model.CategoryTest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryTestDTO {

    private Long id;
    private Long testId;
    private Long categoryId;
    private Long status;
    private String part;
    private String pathFile;
    private String categoryName;
    public CategoryTest toModel(){
        CategoryTest categoryTest = new CategoryTest();
        categoryTest.setId(this.id);
        categoryTest.setTestId(this.testId);
        categoryTest.setCategoryId(this.categoryId);
        categoryTest.setStatus(this.status);
        categoryTest.setPart(this.part);
        categoryTest.setPathFile(this.pathFile);
        categoryTest.setCategoryName(this.categoryName);
        return categoryTest;
    }
}
