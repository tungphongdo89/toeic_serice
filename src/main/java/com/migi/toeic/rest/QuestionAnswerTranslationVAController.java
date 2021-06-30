package com.migi.toeic.rest;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.dto.QuestionAnswersTranslationVADTO;
import com.migi.toeic.service.impl.QuestionAnswerTranslationVAServiceImpl;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@Api(description = "API QUESTION MANAGEMENT")

@RequestMapping("/v1/questions/translationVA")
@JsonRootName("snapshot")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class QuestionAnswerTranslationVAController {

    @Autowired
    QuestionAnswerTranslationVAServiceImpl questionAnswerTranslationVAServiceImp;

    @PostMapping(value = "/doSearch")
    public ResponseEntity<?> doSearchQuestionTransVA(@RequestBody QuestionAnswersTranslationVADTO obj) {
        DataListDTO data = questionAnswerTranslationVAServiceImp.getListQuestionTransVA(obj);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @PostMapping(value = "/getDetail")
    public ResponseEntity<?> getDetail(@RequestBody Long topicId) {
        QuestionAnswersTranslationVADTO questionAnswerTranslationVA = questionAnswerTranslationVAServiceImp.getDetail(topicId);
        return new ResponseEntity<>(questionAnswerTranslationVA, HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<?> create(@RequestBody QuestionAnswersTranslationVADTO obj) {
        return new ResponseEntity<>(questionAnswerTranslationVAServiceImp.createQATransVA(obj), HttpStatus.OK);
    }

    @PostMapping(value = "/update")
    public ResponseEntity<?> update(@RequestBody QuestionAnswersTranslationVADTO obj) {
        return new ResponseEntity<>(questionAnswerTranslationVAServiceImp.updateQATransVA(obj), HttpStatus.OK);
    }

    @PostMapping(value = "/delete")
    public ResponseEntity<?> delete(@RequestBody QuestionAnswersTranslationVADTO obj) {
        return new ResponseEntity<>(questionAnswerTranslationVAServiceImp.deleteQATransVA(obj), HttpStatus.OK);
    }

    @PostMapping(value = "/getListQaTransVAByTopic")
    public ResponseEntity<?> getListQaTransAVByTopic(@RequestBody QuestionAnswersTranslationVADTO obj) {
        return new ResponseEntity<>(questionAnswerTranslationVAServiceImp.getListQaTransVAByTopic(obj), HttpStatus.OK);
    }

    @PostMapping(value = "/getListQaTransVAByLevel")
    public ResponseEntity<?> getListQaTransAVByLevel(@RequestBody QuestionAnswersTranslationVADTO obj) {
        return new ResponseEntity<>(questionAnswerTranslationVAServiceImp.getListQaTransVAByLevel(obj), HttpStatus.OK);
    }
}

