//package com.migi.toeic.config;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.context.request.WebRequest;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
//
//@ControllerAdvice
//@RestController
//public class CustomizedResponseEntityAllExceptionHandler extends ResponseEntityExceptionHandler {
//	@ExceptionHandler(Exception.class)
//	public final ResponseEntity<?> handleAllExceptions(Exception ex, WebRequest request) {
//		return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//	}
//}
