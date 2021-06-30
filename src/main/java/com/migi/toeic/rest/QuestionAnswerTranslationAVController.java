package com.migi.toeic.rest;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.dto.QuestionAnswersTranslationAVDTO;
import com.migi.toeic.service.impl.QuestionAnswerTranslationAVServiceImpl;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@Api(description = "API QUESTION MANAGEMENT")

@RequestMapping("/v1/questions/translationAV")
@JsonRootName("snapshot")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class QuestionAnswerTranslationAVController {

    @Autowired
    QuestionAnswerTranslationAVServiceImpl questionAnswerTranslationAVServiceImp;

    @PostMapping(value = "/doSearch")
    public ResponseEntity<?> doSearchQuestionTransAV(@RequestBody QuestionAnswersTranslationAVDTO obj) {
        DataListDTO data = questionAnswerTranslationAVServiceImp.getListQuestionTransAV(obj);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @PostMapping(value = "/getDetail")
    public ResponseEntity<?> getDetail(@RequestBody Long topicId) {
        QuestionAnswersTranslationAVDTO questionAnswerTranslation = questionAnswerTranslationAVServiceImp.getDetail(topicId);
        return new ResponseEntity<>(questionAnswerTranslation, HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<?> create(@RequestBody QuestionAnswersTranslationAVDTO obj) {
        return new ResponseEntity<>(questionAnswerTranslationAVServiceImp.createQATransAV(obj), HttpStatus.OK);
    }

    @PostMapping(value = "/update")
    public ResponseEntity<?> update(@RequestBody QuestionAnswersTranslationAVDTO obj) {
        return new ResponseEntity<>(questionAnswerTranslationAVServiceImp.updateQATransAV(obj), HttpStatus.OK);
    }

    @PostMapping(value = "/delete")
    public ResponseEntity<?> delete(@RequestBody QuestionAnswersTranslationAVDTO obj) {
        return new ResponseEntity<>(questionAnswerTranslationAVServiceImp.deleteQATransAV(obj), HttpStatus.OK);
    }
    
    @PostMapping(value = "/getListQaTransAVByTopic")
    public ResponseEntity<?> getListQaTransAVByTopic(@RequestBody QuestionAnswersTranslationAVDTO obj) {
        return new ResponseEntity<>(questionAnswerTranslationAVServiceImp.getListQaTransAVByTopic(obj), HttpStatus.OK);
    }

    @PostMapping(value = "/getListQaTransAVByLevel")
    public ResponseEntity<?> getListQaTransAVByLevel(@RequestBody QuestionAnswersTranslationAVDTO obj) {
        return new ResponseEntity<>(questionAnswerTranslationAVServiceImp.getListQaTransAVByLevel(obj), HttpStatus.OK);
    }
    
}

