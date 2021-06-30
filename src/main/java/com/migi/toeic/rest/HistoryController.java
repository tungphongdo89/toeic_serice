package com.migi.toeic.rest;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.migi.toeic.authen.JwtTokenUtil;
import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.dto.*;
import com.migi.toeic.service.impl.HistoryServiceImpl;
import com.migi.toeic.service.impl.UserServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Api(description = "url history test ")
@RequestMapping(value = "/v1/testLog")
@JsonRootName("snapshot")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class HistoryController {

    @Autowired
    private HistoryServiceImpl historyService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserServiceImpl userService;


    @PostMapping(value = "/doSearch")
    public ResponseEntity<?> doSearch(@RequestBody  HistoryDTO historyDTO)
    {
        DataListDTO dataListDTO=historyService.doSearch(historyDTO);
        return new ResponseEntity<>(dataListDTO, HttpStatus.OK);
    }

    @PostMapping(value = "/getListFail")
    public ResponseEntity<?> getListFail(@RequestBody  HistoryDTO historyDTO) throws Exception{
        DataListDTO lst = historyService.listStudentFaild(historyDTO);
        return new ResponseEntity<>(lst,HttpStatus.OK);
    }
    @PostMapping(value = "/getDetailHistoryTest")
    public ResponseEntity<?> getDetailHistoryTest(@RequestBody  HistoryDTO historyDTO)
    {
        DataListDTO dataListDTO=historyService.getDetailHistoryTest(historyDTO);
        return new ResponseEntity<>(dataListDTO, HttpStatus.OK);
    }
    @PostMapping(value = "/getListRankOfTest")
    public ResponseEntity<?> getListRankOfTest(@RequestBody  HistoryDTO historyDTO)
    {
        List<HistoryDTO> dataListDTO=historyService.getListRankOfTest(historyDTO);
        return new ResponseEntity<>(dataListDTO, HttpStatus.OK);
    }
    @PostMapping(value = "/getDetailHistoryFullTest")
    public ResponseEntity<?> getDetailHistoryFullTest(@RequestBody  HistoryDTO historyDTO)
    {
        List<MinitestDTO> dataListDTO=historyService.getDetailHistoryFullTest(historyDTO);
        return new ResponseEntity<>(dataListDTO, HttpStatus.OK);
    }

    @PostMapping(value = "/getDetailHistoryLF")
    public ResponseEntity<?> getDetailHistoryLF(@RequestBody HistoryPracticesDTO historyPracticesDTO)
    {
        List<QuestionAnswersDTO> dataListDTO=historyService.getDetailHistoryListenFill(historyPracticesDTO);
        return new ResponseEntity<>(dataListDTO, HttpStatus.OK);
    }
    @PostMapping(value = "/getDetailHistoryReadingFill")
    public ResponseEntity<?> getDetailHistoryReadingFill(@RequestBody HistoryPracticesDTO historyPracticesDTO)
    {
        List<QuestionAnswersDTO> dataListDTO=historyService.getDetailHistoryReadWordFill(historyPracticesDTO);
        return new ResponseEntity<>(dataListDTO, HttpStatus.OK);
    }
    @PostMapping(value = "/getDetailHistoryReadingCompletedPassage")
    public ResponseEntity<?> getDetailHistoryReadingCompletedPassage(@RequestBody HistoryPracticesDTO historyPracticesDTO)
    {
        List<QuestionOfReadingAndComplitingDTO> dataListDTO=historyService.getDetailHistoryReadingCompletedPassage(historyPracticesDTO);
        return new ResponseEntity<>(dataListDTO, HttpStatus.OK);
    }
}
