package com.migi.toeic.service;

import com.migi.toeic.authen.model.RequestPractice;
import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.dto.CategoryDTO;
import com.migi.toeic.dto.HistoryPracticesDTO;
import com.migi.toeic.dto.QuestionAnswerListeningDTO;
import com.migi.toeic.dto.QuestionAnswersDTO;

import java.util.List;

public interface QuestionAnswerListeningService {
     DataListDTO getListQuestionListening(QuestionAnswerListeningDTO obj);
     QuestionAnswerListeningDTO getDetail(Long id);
     Long createQAListening(QuestionAnswerListeningDTO obj);
     Long updateQAListening(QuestionAnswerListeningDTO obj);
     Long deleteQAListening(QuestionAnswerListeningDTO obj);
     List<QuestionAnswerListeningDTO> listQuestionByTopicIdAndLevelCode(RequestPractice requestPractice);
     QuestionAnswerListeningDTO getAnswerListeningQuestion(RequestPractice rp);
     List<QuestionAnswerListeningDTO> getListQuestionOfListenAndFill(RequestPractice requestPractice);
     QuestionAnswersDTO getResultQuestionOfListenWordFill(QuestionAnswerListeningDTO obj);
     List<RequestPractice> getPracticeConversation(RequestPractice rp);
     String createHistoryListenFill(HistoryPracticesDTO historyPracticesDTO);
     Long insertData(QuestionAnswerListeningDTO listeningDTO);
}
