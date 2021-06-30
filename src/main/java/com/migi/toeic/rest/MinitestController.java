package com.migi.toeic.rest;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.dto.HistoryMinitestDTO;
import com.migi.toeic.dto.MinitestResultHistoryDTO;
import com.migi.toeic.dto.MinitestSubmitAnswerDTO;
import com.migi.toeic.service.impl.MinitestServiceImpl;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@Api(description = "API QUESTION MANAGEMENT")

@RequestMapping("/v1/questions/minitest")
@JsonRootName("snapshot")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class  MinitestController {
    @Autowired
    private MinitestServiceImpl minitestServiceImpl;

    @PostMapping(value = "/getListQuestionMinitest")
    public ResponseEntity<Object> getListQuestionMinitest(){
        return new ResponseEntity<>(minitestServiceImpl.getListQuestionMinitest(),HttpStatus.OK);
    }

    @PostMapping(value = "/submitAnswer")  //consumes = "multipart/form-data"
    public ResponseEntity<Object> submitAnswer(@RequestBody MinitestSubmitAnswerDTO minitestSubmitAnswerDTO){
        return new ResponseEntity<>(minitestServiceImpl.getListQuestionMinitestChoosenAnswer(minitestSubmitAnswerDTO),HttpStatus.OK);
    }

    @PostMapping(value = "/saveResultMinitest")
    public ResponseEntity<?> saveResultMinitest(@RequestBody MinitestResultHistoryDTO minitestResultHistoryDTO){
        return new ResponseEntity<>(minitestServiceImpl.saveResultMinitest(minitestResultHistoryDTO), HttpStatus.OK);
    }

    @PostMapping(value = "/getListHistoryMinitest")
    public ResponseEntity<?> getListHistoryMinitest(@RequestBody HistoryMinitestDTO historyMinitestDTO){
        return new ResponseEntity<>(minitestServiceImpl.getListHistoryMinitest(historyMinitestDTO), HttpStatus.OK);
    }

    @PostMapping(value = "/doSearch")
    public ResponseEntity<?> doSearch(@RequestBody HistoryMinitestDTO historyMinitestDTO){

        DataListDTO dataListDTO = minitestServiceImpl.doSearch(historyMinitestDTO);

        return new ResponseEntity<>(dataListDTO, HttpStatus.OK);
    }
}

