package com.migi.toeic.service;


import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.dto.HistoryMinitestDTO;
import com.migi.toeic.dto.MinitestDTO;
import com.migi.toeic.dto.MinitestSubmitAnswerDTO;

import java.util.List;

public interface MinitestService {
    public List<MinitestDTO> getListQuestionMinitest();
    public List<MinitestDTO> getListQuestionMinitestChoosenAnswer(MinitestSubmitAnswerDTO minitestSubmitAnswerDTO);
    public List<HistoryMinitestDTO> getListHistoryMinitest(HistoryMinitestDTO historyMinitestDTO);
    public DataListDTO doSearch(HistoryMinitestDTO historyMinitestDTO);

}
