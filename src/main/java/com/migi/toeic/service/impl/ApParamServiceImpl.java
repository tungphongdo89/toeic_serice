package com.migi.toeic.service.impl;

import com.migi.toeic.dto.ApParamDTO;
import com.migi.toeic.dto.ApParamTestDTO;
import com.migi.toeic.respositories.ApParamRepository;
import com.migi.toeic.service.ApParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApParamServiceImpl implements ApParamService {
    @Autowired
    private ApParamRepository apParamRepository;

    @Override
    public List<ApParamDTO> getType()
    {
        return apParamRepository.getType();
    }
    @Override
    public List<ApParamDTO> getPart(String parentCode)
    {
        return apParamRepository.getPartByParentCode(parentCode);

    }
    @Override
    public List<ApParamDTO> getLevelCode(){
        return apParamRepository.getLevelCode();
    }

    @Override
    public List<ApParamTestDTO> getMenuListTest() {
        List<ApParamTestDTO> listTypeTest = new ArrayList<>();
        List<ApParamTestDTO> listRoot = new ArrayList<>();
        listRoot = apParamRepository.getMenuListTest();

        for (ApParamTestDTO apParamTestDTO : listRoot) {
            List<ApParamTestDTO> listChild = apParamRepository.getMenuListTestByParentCode(apParamTestDTO.getTypeTestCode());
            ApParamTestDTO apParamTestDTO2 = new ApParamTestDTO();
            apParamTestDTO2.setTypeTestName(apParamTestDTO.getTypeTestName());
            apParamTestDTO2.setTypeTestCode(apParamTestDTO.getTypeTestCode());
            apParamTestDTO2.setTypeTestValue(apParamTestDTO.getTypeTestValue());
            apParamTestDTO2.setListApp(listChild);

            listTypeTest.add(apParamTestDTO2);
        }
        return listTypeTest;
    }


}
