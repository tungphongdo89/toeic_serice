package com.migi.toeic.service.impl;

import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.base.ResultDataDTO;
import com.migi.toeic.dto.QuestionAnswersTranslationAVDTO;
import com.migi.toeic.dto.QuestionAnswersTranslationVADTO;
import com.migi.toeic.exception.BusinessException;
import com.migi.toeic.respositories.QuestionAnswerTranslationVARepository;
import com.migi.toeic.service.QuestionAnswerTranslationVAService;
import com.migi.toeic.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionAnswerTranslationVAServiceImpl implements QuestionAnswerTranslationVAService {
    
    @Autowired
    QuestionAnswerTranslationVARepository questionAnswerTranslationVARepository;

    @Override
    public DataListDTO getListQuestionTransVA(QuestionAnswersTranslationVADTO obj) {
        ResultDataDTO resultDto = questionAnswerTranslationVARepository.getListQuestionTransVA(obj);
        DataListDTO data = new DataListDTO();
        data.setData(resultDto.getData());
        data.setTotal(resultDto.getTotal());
        data.setSize(resultDto.getTotal());
        data.setStart(1);
        return data;
    }

    @Override
    public QuestionAnswersTranslationVADTO getDetail(Long id) {
        if(id == null){
            throw new BusinessException(MessageUtils.getMessage("error_miss_info"));
        }
        return questionAnswerTranslationVARepository.getDetail(id);
    }

    @Override
    public Long createQATransVA(QuestionAnswersTranslationVADTO obj) {
        if(obj.getName() == null || obj.getAnswer() == null || obj.getParentId() == null || obj.getStatus() == null || obj.getAnswersToChoose() == null){
            throw new BusinessException(MessageUtils.getMessage("error_miss_info"));
        }
        return questionAnswerTranslationVARepository.insert(obj.toModel());
    }

    @Override
    public Long updateQATransVA(QuestionAnswersTranslationVADTO obj) {
        if(obj.getName() == null || obj.getAnswer() == null || obj.getParentId() == null || obj.getStatus() == null || obj.getAnswersToChoose() == null){
            throw new BusinessException(MessageUtils.getMessage("error_miss_info"));
        }
        questionAnswerTranslationVARepository.update(obj.toModel());
        return 1L;
    }

    @Override
    public Long deleteQATransVA(QuestionAnswersTranslationVADTO obj) {
        questionAnswerTranslationVARepository.delete(obj.toModel());
        return 1L;
    }

    @Override
    public List<QuestionAnswersTranslationVADTO> getListQaTransVAByTopic(QuestionAnswersTranslationVADTO obj) {
        List<QuestionAnswersTranslationVADTO> lists = questionAnswerTranslationVARepository.getListQaTransVAByTopic(obj);
        return lists;
    }

    @Override
    public List<QuestionAnswersTranslationVADTO> getListQaTransVAByLevel(QuestionAnswersTranslationVADTO obj) {

        List<QuestionAnswersTranslationVADTO> lists = new ArrayList<>();

        //Trình độ dễ
        if(obj.getParentId() == 104){
            lists = questionAnswerTranslationVARepository.getListQaTransVAByLevel(obj, 3);
        }

        //Trình độ TB
        if(obj.getParentId() == 105){
            obj.setParentId((long)104);
            List<QuestionAnswersTranslationVADTO> listEasy =
                    questionAnswerTranslationVARepository.getListQaTransVAByLevel(obj, 2);

            obj.setParentId((long)105);
            List<QuestionAnswersTranslationVADTO> listMedium =
                    questionAnswerTranslationVARepository.getListQaTransVAByLevel(obj, 1);

            lists.add(listEasy.get(0));
            lists.add(listEasy.get(1));
            lists.add(listMedium.get(0));
        }

        //Trình độ Khó
        if(obj.getParentId() == 106){
            obj.setParentId((long)104);
            List<QuestionAnswersTranslationVADTO> listEasy =
                    questionAnswerTranslationVARepository.getListQaTransVAByLevel(obj, 1);

            obj.setParentId((long)105);
            List<QuestionAnswersTranslationVADTO> listMedium =
                    questionAnswerTranslationVARepository.getListQaTransVAByLevel(obj, 1);

            obj.setParentId((long)106);
            List<QuestionAnswersTranslationVADTO> listDifficult =
                    questionAnswerTranslationVARepository.getListQaTransVAByLevel(obj, 1);

            lists.add(listEasy.get(0));
            lists.add(listMedium.get(0));
            lists.add(listDifficult.get(0));
        }

        return lists;
    }


}

