package com.migi.toeic.service;

import com.migi.toeic.dto.ObjectDTO;
import com.migi.toeic.dto.RoleDTO;

import java.util.List;

public interface ObjectService {

    public List<ObjectDTO> getObjectByUserId(Long userId);
}
