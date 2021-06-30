package com.migi.toeic.service;

import java.util.List;

import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.dto.QuestionAnswersTranslationAVDTO;

public interface QuestionAnswerTranslationAVService {
    public DataListDTO getListQuestionTransAV(QuestionAnswersTranslationAVDTO obj);
    public QuestionAnswersTranslationAVDTO getDetail(Long id);
    public Long createQATransAV(QuestionAnswersTranslationAVDTO obj);
    public Long updateQATransAV(QuestionAnswersTranslationAVDTO obj);
    public Long deleteQATransAV(QuestionAnswersTranslationAVDTO obj);
    public List<QuestionAnswersTranslationAVDTO> getListQaTransAVByTopic(QuestionAnswersTranslationAVDTO obj);
    public List<QuestionAnswersTranslationAVDTO> getListQaTransAVByLevel(QuestionAnswersTranslationAVDTO obj);
}
