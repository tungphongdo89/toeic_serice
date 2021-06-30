package com.migi.toeic.service;

import com.migi.toeic.authen.model.RequestPractice;
import com.migi.toeic.base.DataListDTO;
import java.util.List;

public interface DoPracticeService {
    public DataListDTO doPractices(RequestPractice requestPractice);
    public DataListDTO doViewResult(Long tag);

}
