package com.migi.toeic.rest;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.migi.toeic.authen.model.RequestPractice;
import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.dto.CategoryDTO;
import com.migi.toeic.dto.HistoryPracticesDTO;
import com.migi.toeic.dto.QuestionAnswersReadingDTO;
import com.migi.toeic.dto.TopicDTO;
import com.migi.toeic.service.impl.QuestionAnswerReadingServiceImpl;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@Api(description = "API QUESTION MANAGEMENT")

@RequestMapping("/v1/questions/reading")
@JsonRootName("snapshot")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class QuestionAnswerReadingController {

    @Autowired
    QuestionAnswerReadingServiceImpl questionAnswerReadingServiceImp;

    @PostMapping(value = "/doSearch")
    public ResponseEntity<?> doSearchQuestionReading(@RequestBody QuestionAnswersReadingDTO obj) {
        DataListDTO data = questionAnswerReadingServiceImp.getListQuestionReading(obj);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @PostMapping(value = "/getDetail")
    public ResponseEntity<?> getDetail(@RequestBody Long topicId) {
        QuestionAnswersReadingDTO questionAnswerReading = questionAnswerReadingServiceImp.getDetail(topicId);
        return new ResponseEntity<>(questionAnswerReading, HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<?> create(@RequestBody QuestionAnswersReadingDTO obj) {
        return new ResponseEntity<>(questionAnswerReadingServiceImp.createQAReading(obj), HttpStatus.OK);
    }

    @PostMapping(value = "/update")
    public ResponseEntity<?> update(@RequestBody QuestionAnswersReadingDTO obj) {
        return new ResponseEntity<>(questionAnswerReadingServiceImp.updateQAReading(obj), HttpStatus.OK);
    }

    @PostMapping(value = "/delete")
    public ResponseEntity<?> delete(@RequestBody QuestionAnswersReadingDTO obj) {
        return new ResponseEntity<>(questionAnswerReadingServiceImp.deleteQAReading(obj), HttpStatus.OK);
    }

    @PostMapping(value = "/getListQuestionsOfRead")
    public ResponseEntity<?>getListQuestionsOfRead(@RequestBody Long categoryId)throws Exception{
        return new ResponseEntity<>(questionAnswerReadingServiceImp.getListQuestionsOfRead(categoryId),HttpStatus.OK);
    }
    @PostMapping(value = "/getListQuestionsOfReadWordFill")
    public ResponseEntity<?> getListQuestionsOfReadWordFill(@RequestBody RequestPractice requestPractice) {
        return new ResponseEntity<>(questionAnswerReadingServiceImp.getListQuestionsOfReadWordFill(requestPractice), HttpStatus.OK);
    }
    @PostMapping(value = "/getResultQuestionOfReadWordFill")
    public ResponseEntity<?> getResultQuestionOfReadWordFill(@RequestBody QuestionAnswersReadingDTO obj) {
        return new ResponseEntity<>(questionAnswerReadingServiceImp.getResultQuestionOfReadWordFill(obj), HttpStatus.OK);
    }
    @PostMapping(value = "/getListQuestionSingleOrDual")
    public ResponseEntity<?> getListQuestionSingleOrDual(@RequestBody RequestPractice requestPractice) {
        return new ResponseEntity<>(questionAnswerReadingServiceImp.getListQuestionSingleOrDual(requestPractice), HttpStatus.OK);
    }
    @PostMapping(value = "/submitListQuestionSingleOrDual")
    public ResponseEntity<?> submitListQuestionSingleOrDual(@RequestBody CategoryDTO categoryDTO) {
        return new ResponseEntity<>(questionAnswerReadingServiceImp.submitListQuestionSingleOrDual(categoryDTO), HttpStatus.OK);
    }
    @PostMapping(value = "/createHistoryReadingWordFill")
    public ResponseEntity<?> createHistoryReadingWordFill(@RequestBody HistoryPracticesDTO historyPracticesDTO){
        String mess = questionAnswerReadingServiceImp.createHistoryReadWordFill(historyPracticesDTO);
        return new ResponseEntity<>(mess, HttpStatus.OK);
    }
}

