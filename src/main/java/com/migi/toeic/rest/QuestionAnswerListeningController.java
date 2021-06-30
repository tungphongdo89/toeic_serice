package com.migi.toeic.rest;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.migi.toeic.authen.model.RequestPractice;
import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.dto.HistoryPracticesDTO;
import com.migi.toeic.dto.QuestionAnswerListeningDTO;
import com.migi.toeic.service.impl.QuestionAnswerListeningServiceImpl;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@Api(description = "API QUESTION MANAGEMENT")

@RequestMapping("/v1/questions/listening")
@JsonRootName("snapshot")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class QuestionAnswerListeningController {

    @Autowired
    QuestionAnswerListeningServiceImpl questionAnswerListeningServiceImp;

    @PostMapping(value = "/doSearch")
    public ResponseEntity<?> doSearchQuestionListening(@RequestBody QuestionAnswerListeningDTO obj) {
        DataListDTO data = questionAnswerListeningServiceImp.getListQuestionListening(obj);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @PostMapping(value = "/getDetail")
    public ResponseEntity<?> getDetail(@RequestBody Long topicId) {
        QuestionAnswerListeningDTO questionAnswerListening = questionAnswerListeningServiceImp.getDetail(topicId);
        return new ResponseEntity<>(questionAnswerListening, HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<?> create(@RequestBody QuestionAnswerListeningDTO obj) {
        return new ResponseEntity<>(questionAnswerListeningServiceImp.createQAListening(obj), HttpStatus.OK);
    }

    @PostMapping(value = "/update")
    public ResponseEntity<?> update(@RequestBody QuestionAnswerListeningDTO obj) {
        return new ResponseEntity<>(questionAnswerListeningServiceImp.updateQAListening(obj), HttpStatus.OK);
    }

    @PostMapping(value = "/delete")
    public ResponseEntity<?> delete(@RequestBody QuestionAnswerListeningDTO obj) {
        return new ResponseEntity<>(questionAnswerListeningServiceImp.deleteQAListening(obj), HttpStatus.OK);
    }

    @PostMapping(value = "/getListQuestionByTopicId")
    public ResponseEntity<?> getListQuestionByTopicId(@RequestBody RequestPractice requestPractice) {
        return new ResponseEntity<>(questionAnswerListeningServiceImp.listQuestionByTopicIdAndLevelCode(requestPractice), HttpStatus.OK);
    }

    @PostMapping(value = "/getListQuestionOfListenAndFill")
    public ResponseEntity<?> getListQuestionOfListenAndFill(@RequestBody RequestPractice requestPractice) {
        return new ResponseEntity<>(questionAnswerListeningServiceImp.getListQuestionOfListenAndFill(requestPractice), HttpStatus.OK);
    }

    @PostMapping(value = "/getResultQuestionOfListenWordFill")
    public ResponseEntity<?> getResultQuestionOfListenWordFill(@RequestBody QuestionAnswerListeningDTO obj) {
        return new ResponseEntity<>(questionAnswerListeningServiceImp.getResultQuestionOfListenWordFill(obj), HttpStatus.OK);
    }

    @PostMapping(value = "/checkAnswermultiListeningQuestion")
    public ResponseEntity<?> checkAnswermultiListeningQuestion(@RequestBody RequestPractice rp){
        return new ResponseEntity<>(questionAnswerListeningServiceImp.getAnswerListeningQuestion(rp),HttpStatus.OK);
    }

    @PostMapping(value = "/getPracticeConversation")
    public ResponseEntity<?>getPracticeConversationAndShortStory(@RequestBody RequestPractice rp){
        return new ResponseEntity<>(questionAnswerListeningServiceImp.getPracticeConversation(rp),HttpStatus.OK);
    }

    @PostMapping(value = "/createHistoryListenFill")
    public ResponseEntity<?> createHistoryListenFill(@RequestBody HistoryPracticesDTO historyPracticesDTO){
        String mess = questionAnswerListeningServiceImp.createHistoryListenFill(historyPracticesDTO);
        return new ResponseEntity<>(mess, HttpStatus.OK);
    }
}

