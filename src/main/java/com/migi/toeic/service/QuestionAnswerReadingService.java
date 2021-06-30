package com.migi.toeic.service;

import com.migi.toeic.authen.model.RequestPractice;
import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.base.ResultDataDTO;
import com.migi.toeic.dto.CategoryDTO;
import com.migi.toeic.dto.HistoryPracticesDTO;
import com.migi.toeic.dto.QuestionAnswersDTO;
import com.migi.toeic.dto.QuestionAnswersReadingDTO;

import java.util.List;

public interface QuestionAnswerReadingService {
	DataListDTO getListQuestionReading(QuestionAnswersReadingDTO obj);

	QuestionAnswersReadingDTO getDetail(Long id);

	Long createQAReading(QuestionAnswersReadingDTO obj);

	Long updateQAReading(QuestionAnswersReadingDTO obj);

	Long deleteQAReading(QuestionAnswersReadingDTO obj);

	ResultDataDTO getListQuestionsOfRead(Long categoryId);

	List<QuestionAnswersReadingDTO> getListQuestionsOfReadWordFill(RequestPractice requestPractice);

	QuestionAnswersDTO getResultQuestionOfReadWordFill(QuestionAnswersReadingDTO obj);

	List<CategoryDTO> getListQuestionSingleOrDual(RequestPractice requestPractice);

	CategoryDTO submitListQuestionSingleOrDual(CategoryDTO categoryDTO);

	String createHistoryReadWordFill(HistoryPracticesDTO historyPracticesDTO);

	Long insertData(QuestionAnswersReadingDTO dto);
}
