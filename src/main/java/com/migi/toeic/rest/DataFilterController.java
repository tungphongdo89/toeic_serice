package com.migi.toeic.rest;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.migi.toeic.dto.ApParamDTO;
import com.migi.toeic.dto.TopicDTO;
import com.migi.toeic.respositories.ApParamRepository;
import com.migi.toeic.service.impl.ApParamServiceImpl;
import com.migi.toeic.service.impl.TopicServiceImpl;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(description = "Data filter")

@RequestMapping("/v1/data")
@JsonRootName("snapshot")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DataFilterController {

    @Autowired
    private ApParamServiceImpl apParamServiceImpl;
    @Autowired
    private TopicServiceImpl topicServiceImpl;

    @PostMapping(value = "/getTypeTopic")
    public ResponseEntity<?> getDataFilterTypeTopic(@RequestBody ApParamDTO a){
        List<ApParamDTO> lstTypeTopic= apParamServiceImpl.getType();
        return new ResponseEntity<>(lstTypeTopic, HttpStatus.OK);
    }
    @PostMapping(value = "/getPartByTopic")
    public ResponseEntity<?> getPartByTopic(@RequestBody ApParamDTO apParamDTO){

        List<ApParamDTO> lstPartByTopic = apParamServiceImpl.getPart(apParamDTO.getParentCode());
        return new ResponseEntity<>(lstPartByTopic, HttpStatus.OK);
    }
    @PostMapping(value = "/getTopicByTypeTopicAndPart")
    public ResponseEntity<?> getTopicByTypeTopicAndPart(@RequestBody TopicDTO topicDTO){
            List<TopicDTO> lstTopicByPartAndTypeTopic = topicServiceImpl.getListTopicName(topicDTO.getPartTopicCode(),topicDTO.getTypeTopicCode());;
        return new ResponseEntity<>(lstTopicByPartAndTypeTopic,HttpStatus.OK);
    }
}
