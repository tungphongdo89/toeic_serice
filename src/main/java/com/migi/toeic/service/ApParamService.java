package com.migi.toeic.service;


import com.migi.toeic.dto.ApParamDTO;
import com.migi.toeic.dto.ApParamTestDTO;

import java.util.List;

public interface ApParamService {
    public List<ApParamDTO> getType();
    public List<ApParamDTO> getPart(String parentCode);
    public List<ApParamDTO> getLevelCode();
    public List<ApParamTestDTO> getMenuListTest();

}
