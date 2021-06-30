package com.migi.toeic.rest;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.migi.toeic.authen.model.RequestPractice;
import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.service.impl.DoPractiesServiceImpl;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(description = "API student practices random ")
@RequestMapping("/v1/practices")
@JsonRootName("snapshot")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PracticeController {
    @Autowired
    private DoPractiesServiceImpl doPracticeService;

    @PostMapping(value = "/random")
    public ResponseEntity<?> doPracticeRandom(@RequestBody RequestPractice requestPractice) {
        DataListDTO dataListDTO = doPracticeService.doPractices(requestPractice);
        return new ResponseEntity<>(dataListDTO, HttpStatus.OK);
    }
    @PostMapping(value = "/viewResultPractice")
    public ResponseEntity<?> viewResultPractice(@RequestBody RequestPractice requestPractice) {
        DataListDTO dataListDTO = doPracticeService.doViewResult(requestPractice.getTypeCode());
        return new ResponseEntity<>(dataListDTO,HttpStatus.OK);
    }
}
