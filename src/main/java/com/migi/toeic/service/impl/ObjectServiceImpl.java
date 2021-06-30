package com.migi.toeic.service.impl;

import com.migi.toeic.dto.ObjectDTO;
import com.migi.toeic.respositories.ObjectRepository;
import com.migi.toeic.service.ObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ObjectServiceImpl implements ObjectService {

    @Autowired
    ObjectRepository objectRepository;

    @Override
    public List<ObjectDTO> getObjectByUserId(Long userId) {
        List<ObjectDTO> lstObject = objectRepository.getObjectByUserID(userId);
        return lstObject;
    }
}
