package com.migi.toeic.model;

import com.migi.toeic.dto.CategoryTestDTO;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "CATEGORY_TEST")
public class CategoryTest implements Serializable {

	@Id
	@SequenceGenerator(sequenceName = "CATEGORY_TEST_seq", allocationSize = 1, name = "category_test")
	@GeneratedValue(generator = "category_test", strategy = GenerationType.SEQUENCE)
	private Long id;
	@Column(name = "TEST_ID")
	private Long testId;
	@Column(name = "CATEGORY_ID")
	private Long categoryId;
	@Column(name = "STATUS")
	private Long status;
	@Column(name = "PART")
	private String part;
	@Column(name = "PATH_FILE")
	private String pathFile;
	@Column(name = "CATEGORY_NAME")
	private String categoryName;

	public CategoryTestDTO toModel() {
		CategoryTestDTO categoryTestDTO = new CategoryTestDTO();
		categoryTestDTO.setId(this.id);
		categoryTestDTO.setTestId(this.testId);
		categoryTestDTO.setCategoryId(this.categoryId);
		categoryTestDTO.setStatus(this.status);
		categoryTestDTO.setPart(this.part);
		categoryTestDTO.setPathFile(this.pathFile);
		return categoryTestDTO;
	}

}
