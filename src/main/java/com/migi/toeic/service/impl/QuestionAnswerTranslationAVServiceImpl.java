package com.migi.toeic.service.impl;

import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.base.ResultDataDTO;
import com.migi.toeic.dto.QuestionAnswersTranslationAVDTO;
import com.migi.toeic.exception.BusinessException;
import com.migi.toeic.respositories.QuestionAnswerTranslationAVRepository;
import com.migi.toeic.service.QuestionAnswerTranslationAVService;
import com.migi.toeic.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionAnswerTranslationAVServiceImpl implements QuestionAnswerTranslationAVService {
    @Autowired
    QuestionAnswerTranslationAVRepository questionAnswerTranslationAVRepository;

    @Override
    public DataListDTO getListQuestionTransAV(QuestionAnswersTranslationAVDTO obj) {
        ResultDataDTO resultDto = questionAnswerTranslationAVRepository.getListQuestionReading(obj);
        DataListDTO data = new DataListDTO();
        data.setData(resultDto.getData());
        data.setTotal(resultDto.getTotal());
        data.setSize(resultDto.getTotal());
        data.setStart(1);

        return data;
    }

    @Override
    public QuestionAnswersTranslationAVDTO getDetail(Long id) {
        if(id == null){
            throw new BusinessException(MessageUtils.getMessage("error_miss_info"));
        }
        return questionAnswerTranslationAVRepository.getDetail(id);
    }

    @Override
    public Long createQATransAV(QuestionAnswersTranslationAVDTO obj) {
        if(obj.getName() == null || obj.getAnswer() == null || obj.getParentId() == null || obj.getStatus() == null || obj.getAnswersToChoose() == null){
            throw new BusinessException(MessageUtils.getMessage("error_miss_info"));
        }
        return questionAnswerTranslationAVRepository.insert(obj.toModel());
    }

    @Override
    public Long updateQATransAV(QuestionAnswersTranslationAVDTO obj) {
        if(obj.getName() == null || obj.getAnswer() == null || obj.getParentId() == null || obj.getStatus() == null || obj.getAnswersToChoose() == null){
            throw new BusinessException(MessageUtils.getMessage("error_miss_info"));
        }
        questionAnswerTranslationAVRepository.update(obj.toModel());
        return 1L;
    }

    @Override
    public Long deleteQATransAV(QuestionAnswersTranslationAVDTO obj) {
        questionAnswerTranslationAVRepository.delete(obj.toModel());
        return 1L;
    }

	@Override
	public List<QuestionAnswersTranslationAVDTO> getListQaTransAVByTopic(QuestionAnswersTranslationAVDTO obj) {
		List<QuestionAnswersTranslationAVDTO> lists = questionAnswerTranslationAVRepository.getListQaTransAVByTopic(obj);
		return lists;
	}

    @Override
    public List<QuestionAnswersTranslationAVDTO> getListQaTransAVByLevel(QuestionAnswersTranslationAVDTO obj) {

        List<QuestionAnswersTranslationAVDTO> lists = new ArrayList<>();

        //Trình độ dễ
        if(obj.getParentId() == 61){
            lists = questionAnswerTranslationAVRepository.getListQaTransAVByLevel(obj, 3);
        }

        //Trình độ TB
        if(obj.getParentId() == 62){
            obj.setParentId((long)61);
            List<QuestionAnswersTranslationAVDTO> listEasy =
                    questionAnswerTranslationAVRepository.getListQaTransAVByLevel(obj, 2);

            obj.setParentId((long)62);
            List<QuestionAnswersTranslationAVDTO> listMedium =
                    questionAnswerTranslationAVRepository.getListQaTransAVByLevel(obj, 1);

            lists.add(listEasy.get(0));
            lists.add(listEasy.get(1));
            lists.add(listMedium.get(0));
        }

        //Trình độ Khó
        if(obj.getParentId() == 63){
            obj.setParentId((long)61);
            List<QuestionAnswersTranslationAVDTO> listEasy =
                    questionAnswerTranslationAVRepository.getListQaTransAVByLevel(obj, 1);

            obj.setParentId((long)62);
            List<QuestionAnswersTranslationAVDTO> listMedium =
                    questionAnswerTranslationAVRepository.getListQaTransAVByLevel(obj, 1);

            obj.setParentId((long)63);
            List<QuestionAnswersTranslationAVDTO> listDifficult =
                    questionAnswerTranslationAVRepository.getListQaTransAVByLevel(obj, 1);

            lists.add(listEasy.get(0));
            lists.add(listMedium.get(0));
            lists.add(listDifficult.get(0));
        }

        return lists;
    }


}

