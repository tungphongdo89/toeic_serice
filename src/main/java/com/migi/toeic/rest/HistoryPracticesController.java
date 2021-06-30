package com.migi.toeic.rest;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.dto.*;
import com.migi.toeic.service.impl.HistoryPracticesServiceImpl;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(description = "url history practices ")
@RequestMapping(value = "/v1/historyPractices")
@JsonRootName("snapshot")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class HistoryPracticesController {


    @Autowired
    private HistoryPracticesServiceImpl historyPracticesServiceImpl;


//    @PostMapping(value = "/doSearch")
//    public ResponseEntity<?> doSearch(@RequestBody  HistoryDTO historyDTO)
//    {
//        DataListDTO dataListDTO=historyService.doSearch(historyDTO);
//        return new ResponseEntity<>(dataListDTO, HttpStatus.OK);
//    }

    @PostMapping(value = "/doSearch")
    public ResponseEntity<?> doSearch(@RequestBody HistoryPracticesDTO historyPracticesDTO){

        DataListDTO dataListDTO = historyPracticesServiceImpl.doSearch(historyPracticesDTO);
        return new ResponseEntity<>(dataListDTO, HttpStatus.OK);
    }

    @PostMapping(value = "/getTypeForHistoryPractices")
    public ResponseEntity<?> getTypeForHistoryPractices(){
        List<ApParamDTO> listTypes = historyPracticesServiceImpl.getTypeForHistoryPractices();
        return new ResponseEntity<>(listTypes, HttpStatus.OK);
    }

    @PostMapping(value = "/getPartForHistoryPractices")
    public ResponseEntity<?> getPartForHistoryPractices(@RequestBody ApparamForGetPartOrTopicDTO apparamForGetPartOrTopicDTO){
        List<ApParamDTO> listParts = historyPracticesServiceImpl.getPartForHistoryPractices(apparamForGetPartOrTopicDTO);
        return new ResponseEntity<>(listParts, HttpStatus.OK);
    }

    @PostMapping(value = "/getListTopicsForHistoryPractices")
    public ResponseEntity<?> getListTopicsForHistoryPractices(@RequestBody ApparamForGetPartOrTopicDTO apparamForGetPartOrTopicDTO){
        List<TopicDTO> listTopics = historyPracticesServiceImpl.getListTopicsForHistoryPractices(apparamForGetPartOrTopicDTO);
        return new ResponseEntity<>(listTopics, HttpStatus.OK);
    }

    @PostMapping(value = "/saveResultHistoryListening")
    public ResponseEntity<?> saveResultHisotryListening(@RequestBody HistoryPracticesDTO historyPracticesDTO){
        return new ResponseEntity<>(historyPracticesServiceImpl.saveResultHisotryListening(historyPracticesDTO), HttpStatus.OK);
    }

    @PostMapping(value = "/getDetailHistoryPracticesListening")
    public ResponseEntity<?> getDetailHistoryPracticesListening(@RequestBody HistoryPracticesDTO historyPracticesDTO){
        return new ResponseEntity<>(historyPracticesServiceImpl.getDetailHistoryPracticesListening(historyPracticesDTO), HttpStatus.OK);
    }

    @PostMapping(value = "/saveResultHistoryLisSingle")
    public ResponseEntity<?> saveResultHistoryLisSingle(@RequestBody HistoryPracticesDTO historyPracticesDTO){
        return new ResponseEntity<>(historyPracticesServiceImpl.saveResultHistoryLisSingle(historyPracticesDTO), HttpStatus.OK);
    }

    @PostMapping(value = "/getDetailHistoryLisSingle")
    public ResponseEntity<?> getDetailHistoryLisSingle(@RequestBody HistoryPracticesDTO historyPracticesDTO){
        return new ResponseEntity<>(historyPracticesServiceImpl.getDetailHistoryLisSingle(historyPracticesDTO), HttpStatus.OK);
    }
    @PostMapping(value = "/getDetailHistoryReadingSingle")
    public ResponseEntity<?> getDetailHistoryReadingSingle(@RequestBody HistoryPracticesDTO historyPracticesDTO){
        return new ResponseEntity<>(historyPracticesServiceImpl.getDetailHistoryReadingSingle(historyPracticesDTO), HttpStatus.OK);
    }



}
