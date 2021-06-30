package com.migi.toeic.service;

import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.dto.*;

import java.util.List;

public interface HistoryService {

    public DataListDTO doSearch(HistoryDTO historyDTO);
    public DataListDTO listStudentFaild(HistoryDTO history);
    public DataListDTO getDetailHistoryTest(HistoryDTO historyDTO);
    public List<HistoryDTO> getListRankOfTest(HistoryDTO historyDTO);
    public List<MinitestDTO> getDetailHistoryFullTest(HistoryDTO historyDTO);
    public List<QuestionAnswersDTO> getDetailHistoryListenFill(HistoryPracticesDTO historyPracticesDTO);
    public List<QuestionAnswersDTO> getDetailHistoryReadWordFill(HistoryPracticesDTO historyPracticesDTO);
    public List<QuestionOfReadingAndComplitingDTO> getDetailHistoryReadingCompletedPassage(HistoryPracticesDTO historyPracticesDTO);
}
