package com.migi.toeic.service;

import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.dto.CategoryDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CategoryService {
	DataListDTO getListCategory(CategoryDTO obj) throws Exception;

	CategoryDTO getDetail(Long id);

	String createCategory(CategoryDTO obj);

	String updateCategory(CategoryDTO obj);

	String deleteCategory(CategoryDTO obj) throws Exception;

	List<CategoryDTO> getListTypeDataInput();

	List<CategoryDTO> getListTypeLevel();

	List<CategoryDTO> readFileCsv(MultipartFile file);

	CategoryDTO getDetailByName(Long id);

	List<CategoryDTO> getListCategoryByName(CategoryDTO categoryDTO);

	Long insertData(CategoryDTO obj);
}
