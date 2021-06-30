package com.migi.toeic.service.impl;

import com.migi.toeic.authen.model.RequestPractice;
import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.base.ResultDataDTO;
import com.migi.toeic.dto.QuestionAnswersDTO;
import com.migi.toeic.respositories.QuestionAnswerRepository;
import com.migi.toeic.service.DoPracticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DoPractiesServiceImpl implements DoPracticeService {

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    private List<Long> listId = null;

    @Override
    public DataListDTO doPractices(RequestPractice requestPractice) {
        ResultDataDTO resultDataDTO = null;
        try {
            resultDataDTO = questionAnswerRepository.doPractices(requestPractice);
        } catch (Exception e) {
            e.printStackTrace();
        }
        DataListDTO dataListDTO = new DataListDTO();
        if (resultDataDTO != null) {
            listId = getListId(resultDataDTO.getData());
            dataListDTO.setData(resultDataDTO.getData());
            dataListDTO.setStart(1);
            dataListDTO.setTotal(resultDataDTO.getData().size());
            dataListDTO.setSize(resultDataDTO.getData().size());
        }
        return dataListDTO;
    }

    public List<Long> getListId(List<QuestionAnswersDTO> lst) {
        List<Long> lstId = new ArrayList<>();
        for (QuestionAnswersDTO x : lst) {
            lstId.add(x.getId());
        }
        return lstId;
    }

    @Override
    public DataListDTO doViewResult(Long tag) {
        ResultDataDTO resultDataDTO = null;
        if (listId != null) {
            try {
                resultDataDTO = questionAnswerRepository.getViewResultPractice(listId, tag);
            } catch (Exception e) {
                e.printStackTrace();
            }
            DataListDTO dataListDTO = new DataListDTO();
            if (resultDataDTO != null) {
                dataListDTO.setData(resultDataDTO.getData());
                dataListDTO.setStart(1);
                dataListDTO.setSize(resultDataDTO.getData().size());
                dataListDTO.setTotal(resultDataDTO.getData().size());
            }
            return dataListDTO;
        } else {
            return null;
        }

    }
}
