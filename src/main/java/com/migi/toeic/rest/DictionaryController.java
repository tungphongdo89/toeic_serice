package com.migi.toeic.rest;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.dto.DictionaryDTO;
import com.migi.toeic.service.impl.DictionaryServiceImpl;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Api(description = "Api of dictionary")
@RequestMapping("/v1/dictionaries")
@JsonRootName("snapshot")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DictionaryController {

    @Autowired
    DictionaryServiceImpl dictionaryService;

//    @GetMapping(name = "/listDictionary")
//    public ResponseEntity<?> getList()
//    {
//        DictionaryDTO dictionaryDTO=new DictionaryDTO();
//        DataListDTO data =dictionaryService.doSearch(dictionaryDTO);
//        return new ResponseEntity<>(data, HttpStatus.OK);
//    }
    @PostMapping(value = "/doSearch")
    public  ResponseEntity<?> doSearch(@RequestBody DictionaryDTO dictionaryDTO)
    {
        DataListDTO dataListDTO = dictionaryService.doSearch(dictionaryDTO);
        return new ResponseEntity<>(dataListDTO,HttpStatus.OK);
    }
    @PostMapping(value = "/insert",consumes = "multipart/form-data")
    public ResponseEntity<?> insertDictionary(@ModelAttribute DictionaryDTO dictionaryDTO)
    {
        String result=dictionaryService.insertDictionary(dictionaryDTO);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
    @PostMapping(value = "/update",consumes = "multipart/form-data")
    public ResponseEntity<?> updateDictionary(@ModelAttribute DictionaryDTO dictionaryDTO)
    {

        String result = dictionaryService.updateDictionary(dictionaryDTO);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
    @PostMapping(value = "/delete")
    public ResponseEntity<?> deleteDictionary(@RequestBody DictionaryDTO dictionaryDTO)
    {
       String res= dictionaryService.deleteDictionary(dictionaryDTO);
        return new ResponseEntity<>(res,HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getDictionaryById(@PathVariable("id")  Long id)
    {
   DictionaryDTO dictionaryDTO= dictionaryService.getDictionaryById(id);
        return new ResponseEntity<>(dictionaryDTO,HttpStatus.OK);
    }
    @PostMapping(value = "/translate")
    public  ResponseEntity<?> getDictionaryByNameEng(@RequestBody DictionaryDTO dictionaryDTO)
    {
        DictionaryDTO dictionaryDTO1 = dictionaryService.getDictionaryByNameEng(dictionaryDTO);
        return new ResponseEntity<>(dictionaryDTO1,HttpStatus.OK);
    }
}
