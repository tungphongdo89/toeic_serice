package com.migi.toeic.rest;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.dto.CategoryDTO;
import com.migi.toeic.dto.MinitestSubmitAnswerDTO;
import com.migi.toeic.dto.TestDTO;
import com.migi.toeic.exception.BusinessException;
import com.migi.toeic.service.impl.ApParamServiceImpl;
import com.migi.toeic.service.impl.TestServiceImpl;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@Api(description = "api test management")
@RequestMapping("/v1/tests")
@JsonRootName("snapshot")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TestController {
	@Autowired
	TestServiceImpl testService;

	@Autowired
	ApParamServiceImpl apParamServiceImpl;

	@PostMapping(value = "/doSearch")
	public ResponseEntity<?> doSearch(@RequestBody TestDTO testDTO) {
		try {
			DataListDTO data = testService.doSearch(testDTO);
			return new ResponseEntity<>(data, HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.status(400).body(e);
		}
	}

	@PostMapping(value = "/delete")
	public ResponseEntity<?> deleteTest(@RequestBody TestDTO obj) {
		try {
			String message = testService.deleteTest(obj);
			return new ResponseEntity<>(message, HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.status(400).body(e);
		}
	}

	@PostMapping(value = "/insert")
	public ResponseEntity<?> insertTest(@RequestBody TestDTO testDTO) {
		Long id = testService.insertTest(testDTO);
		return new ResponseEntity<>(id, HttpStatus.OK);
	}

	@PostMapping(value = "/update")
	public ResponseEntity<?> updateTest(@RequestBody TestDTO testDTO) {
		testService.updateTest(testDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value = "/getDetail")
	public ResponseEntity<?> getDetail(@RequestBody TestDTO testDTO) {
		TestDTO data = testService.getDetailTest(testDTO);
		return new ResponseEntity<>(data, HttpStatus.OK);
	}


	@PostMapping(value = "/getListCompletedStudent")
	public ResponseEntity<?> getCompletedStudent(@RequestBody TestDTO testDTO) {
		DataListDTO data = testService.getCompletedStudent(testDTO);
		return new ResponseEntity<>(data, HttpStatus.OK);
	}

	@PostMapping(value = "/getQuestionReading")
	public ResponseEntity<?> getQuestionReading(@RequestBody TestDTO testDTO) {
		DataListDTO data = testService.getQuestionReading(testDTO);
		return new ResponseEntity<>(data, HttpStatus.OK);
	}

	@PostMapping(value = "/getQuestionListening")
	public ResponseEntity<?> getQuestionListening(@RequestBody TestDTO testDTO) {
		DataListDTO data = testService.getQuestionListening(testDTO);
		return new ResponseEntity<>(data, HttpStatus.OK);
	}

	@PostMapping(value = "/getResultReading")
	public ResponseEntity<?> getResultReading(@RequestBody TestDTO testDTO) {
		DataListDTO data = testService.getQuestionReading(testDTO);
		return new ResponseEntity<>(data, HttpStatus.OK);
	}


	@PostMapping(value = "/getResultListening")
	public ResponseEntity<?> getResultListening(@RequestBody TestDTO testDTO) {
		DataListDTO data = testService.getQuestionListening(testDTO);
		return new ResponseEntity<>(data, HttpStatus.OK);
	}

	@PostMapping(value = "/uploadTemp", consumes = {MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	ResponseEntity uploadFiles(@RequestPart final CategoryDTO dto,  @RequestPart(value = "files", required = false) final MultipartFile[] files)  {
		String message = "";
		try {
			List<String> fileNames = new ArrayList<>();
			CategoryDTO dto1 = dto;
			for (MultipartFile file : files) {
				fileNames.add(file.getOriginalFilename());
			}

			message = "Uploaded the files successfully: " + fileNames;
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			message = "Fail to upload files!";
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
		}
	}

	public static String getFileName(MultivaluedMap<String, String> multivaluedMap) {
		String[] contentDisposition = multivaluedMap.getFirst("Content-Disposition").split(";");
		for (String filename : contentDisposition) {
			if ((filename.trim().startsWith("filename"))) {
				String[] name = filename.split("=");
				String exactFileName = name[1].trim().replaceAll("\"", "");

				DateFormatUtils.format(new Date(), "");
				return exactFileName;
			}
		}
		return "unknownFile";
	}

	@PostMapping(value = "/createTest", consumes = "multipart/form-data")
	public ResponseEntity<?> createTest(@ModelAttribute TestDTO testDTO)throws BusinessException {
		String mess = testService.createTest(testDTO);
		return new ResponseEntity<>(mess, HttpStatus.OK);
	}

	@PostMapping(value = "/getListCategoryTypeReadAndListen")
	public ResponseEntity<?> getListCategoryTypeReadAndListen()throws BusinessException {
		DataListDTO data = testService.getListCategoryTypeReadAndListen();
		return new ResponseEntity<>(data, HttpStatus.OK);
	}

	//Lấy danh sách bài test để chọn bài muốn làm.
	@PostMapping(value = "/getMenuListTest")
	public ResponseEntity<?> getMenuListTest() {
		return new ResponseEntity<>(apParamServiceImpl.getMenuListTest(), HttpStatus.OK);
	}

	@PostMapping(value = "/getDetailFullTestById")
	public ResponseEntity<?> getDetailFullTestById(@RequestBody TestDTO testDTO){
		return new ResponseEntity<>(testService.getDetailFullTestById(testDTO),HttpStatus.OK);
	}

	@PostMapping(value = "/getResultFullTestById")
	public ResponseEntity<?> getResultDetailFullTestById(@RequestBody MinitestSubmitAnswerDTO minitestSubmitAnswerDTO){
		return new ResponseEntity<>(testService.getResultDetailFullTestById(minitestSubmitAnswerDTO),HttpStatus.OK);
	}

	@PostMapping(value = "/getListNameTest")
	public ResponseEntity<?> getListNameTest(){
		return new ResponseEntity<>(testService.getListNameTest(),HttpStatus.OK);
	}
}
