package com.migi.toeic.service;

import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.dto.CategoryDTO;
import com.migi.toeic.dto.CategoryTestDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CategoryTestService {
	void deleteCategoryTest(CategoryTestDTO obj) throws Exception;

}
