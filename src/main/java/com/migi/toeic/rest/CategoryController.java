package com.migi.toeic.rest;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.migi.toeic.authen.model.RequestPractice;
import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.base.common.DataDTO;
import com.migi.toeic.dto.*;
import com.migi.toeic.service.impl.CategoryServiceImpl;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@Api(description = "API CATEGORY MANAGEMENT")

@RequestMapping("/v1/categories")
@JsonRootName("snapshot")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@PropertySource("classpath:i18n/messages.properties")
public class CategoryController {
    @Autowired
    CategoryServiceImpl categoryServiceImpl;

    @Autowired
    Environment env;

    @PostMapping(value = "/doSearch")
    public ResponseEntity<?> doSearch(@RequestBody CategoryDTO obj) throws Exception {
        DataListDTO data = categoryServiceImpl.getListCategory(obj);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @PostMapping(value = "/getDetail")
    public ResponseEntity<?> getDetail(@RequestBody CategoryDTO obj) {
        CategoryDTO category = categoryServiceImpl.getDetail(obj.getCategoryId());
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @PostMapping(value = "/create" , consumes = "multipart/form-data")
    public ResponseEntity<?> create(@ModelAttribute CategoryDTO obj) {
        return new ResponseEntity<>(categoryServiceImpl.createCategory(obj), HttpStatus.OK);
    }

    @PostMapping(value = "/update" , consumes = "multipart/form-data")
    public ResponseEntity<?> update(@ModelAttribute CategoryDTO obj) {
        return new ResponseEntity<>(categoryServiceImpl.updateCategory(obj), HttpStatus.OK);
    }

    @PostMapping(value = "/delete")
    public ResponseEntity<?> delete(@RequestBody CategoryDTO obj) throws Exception {
        String message = categoryServiceImpl.deleteCategory(obj);
        return new ResponseEntity<>(message,HttpStatus.OK);
    }

    @PostMapping(value = "/getDataFilter")
    public ResponseEntity<?> getDataFilter(@RequestBody DataInputDTO dataInputDTO) {
        DataDTO data = categoryServiceImpl.getData(dataInputDTO);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    //-------------------------------------------------------------------------

    @PostMapping(value = "/getListTypeDataInput")
    public ResponseEntity<?> getListTypeDataInput() {
        List<CategoryDTO> listTypeDataInput = categoryServiceImpl.getListTypeDataInput();
        return new ResponseEntity<>(listTypeDataInput, HttpStatus.OK);
    }

    //-------------------------------------------------------------------------

    @PostMapping(value = "/getListTypeLevel")
    public ResponseEntity<?> getListTypeLevel() {
        List<CategoryDTO> listTypeLevel = categoryServiceImpl.getListTypeLevel();
        return new ResponseEntity<>(listTypeLevel, HttpStatus.OK);
    }

    @PostMapping(value = "/importCategoryByCsv", consumes = "multipart/form-data")
    public ResponseEntity<?> testCsv(@RequestBody MultipartFile file) throws Exception {
        List<CategoryDTO> data = categoryServiceImpl.readFileCsv(file);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @PostMapping(value = "/getListQuestionOfReadingAndCompliting")
    public ResponseEntity<?> getListQuestionOfReadingAndCompliting(@RequestBody RequestPractice requestPractice) {
        return new ResponseEntity<>(categoryServiceImpl.getListQuestionOfReadingAndCompliting(requestPractice), HttpStatus.OK);
    }

    @PostMapping(value = "/getResultQuestionOfReadingAndCompliting")
    public ResponseEntity<?> getResultQuestionOfReadWordFill(@RequestBody CategoryReadingChoosenDTO obj) {
        return new ResponseEntity<>(categoryServiceImpl.getResultQuestionOfReadingAndCompliting(obj), HttpStatus.OK);
    }

    @PostMapping(value = "/createHistoryReadingAndCompliting")
    public ResponseEntity<?> createHistoryReadingAndCompliting(@RequestBody HistoryPracticesDTO historyPracticesDTO){
        String mess = categoryServiceImpl.createHistoryReadingAndCompliting(historyPracticesDTO);
        return new ResponseEntity<>(mess, HttpStatus.OK);
    }
    @PostMapping(value = "/createHistoryReadingSingleAndDual")
    public ResponseEntity<?> createHistoryReadingSingleAndDual(@RequestBody HistoryPracticesDTO historyPracticesDTO){
        String mess = categoryServiceImpl.createHistoryReadingSingle(historyPracticesDTO);
        return new ResponseEntity<>(mess, HttpStatus.OK);
    }
}

