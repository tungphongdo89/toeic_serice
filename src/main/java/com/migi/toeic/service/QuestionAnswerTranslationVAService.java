package com.migi.toeic.service;

import java.util.List;

import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.dto.QuestionAnswersTranslationVADTO;

public interface QuestionAnswerTranslationVAService {
    public DataListDTO getListQuestionTransVA(QuestionAnswersTranslationVADTO obj);
    public QuestionAnswersTranslationVADTO getDetail(Long id);
    public Long createQATransVA(QuestionAnswersTranslationVADTO obj);
    public Long updateQATransVA(QuestionAnswersTranslationVADTO obj);
    public Long deleteQATransVA(QuestionAnswersTranslationVADTO obj);
    public List<QuestionAnswersTranslationVADTO> getListQaTransVAByTopic(QuestionAnswersTranslationVADTO obj);
    public List<QuestionAnswersTranslationVADTO> getListQaTransVAByLevel(QuestionAnswersTranslationVADTO obj);
}
