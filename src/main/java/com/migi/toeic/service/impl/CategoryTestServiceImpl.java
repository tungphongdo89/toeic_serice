package com.migi.toeic.service.impl;

import com.migi.toeic.dto.CategoryTestDTO;
import com.migi.toeic.respositories.CategoryTestRepository;
import com.migi.toeic.service.CategoryTestService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service

@AllArgsConstructor
@NoArgsConstructor
public class CategoryTestServiceImpl implements CategoryTestService {

	@Autowired
	CategoryTestRepository categoryTestRepository;

	@Override
	@Transactional
	public void deleteCategoryTest(CategoryTestDTO obj) {
		List<CategoryTestDTO> lst = categoryTestRepository.getCategoryTestByTestIdAndCategoryName(obj);
		for(CategoryTestDTO categoryTestDTO:lst){
			categoryTestRepository.delete(categoryTestDTO.toModel());
		}
	}
}
