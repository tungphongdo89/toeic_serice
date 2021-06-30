package com.migi.toeic.service;

import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.dto.*;

import java.util.List;

public interface HistoryPracticesService {

    public DataListDTO doSearch(HistoryPracticesDTO historyPracticesDTO);
    List<ApParamDTO> getTypeForHistoryPractices();
    List<ApParamDTO> getPartForHistoryPractices(ApparamForGetPartOrTopicDTO apparamForGetPartOrTopicDTO);
    List<TopicDTO> getListTopicsForHistoryPractices(ApparamForGetPartOrTopicDTO apparamForGetPartOrTopicDTO);

}
