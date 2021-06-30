package com.migi.toeic.service;

import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.dto.MinitestDTO;
import com.migi.toeic.dto.MinitestSubmitAnswerDTO;
import com.migi.toeic.dto.TestDTO;

import java.util.List;

public interface TestService {

	DataListDTO doSearch(TestDTO testDTO);

	Long insertTest(TestDTO testDTO);

	void updateTest(TestDTO testDTO);

	String deleteTest(TestDTO testDTO) throws Exception;

	TestDTO getDetailTest(TestDTO testDTO);

	DataListDTO getCompletedStudent(TestDTO testDTO);

	DataListDTO getQuestionReading(TestDTO testDTO);

	DataListDTO getQuestionListening(TestDTO testDTO);

	DataListDTO getResultReading(TestDTO testDTO);

	DataListDTO getResultListening(TestDTO testDTO);

	String createTest(TestDTO test);

	DataListDTO getListCategoryTypeReadAndListen();

	List<MinitestDTO> getDetailFullTestById(TestDTO testDTO);

	List<MinitestDTO> getResultDetailFullTestById(MinitestSubmitAnswerDTO minitestSubmitAnswerDTO);

	List<TestDTO> getListNameTest();

	TestDTO findDetailByName(TestDTO obj);
}
