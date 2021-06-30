//package com.migi.toeic.rest;
//
//import com.fasterxml.jackson.annotation.JsonRootName;
//import io.swagger.annotations.Api;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@Api(description = "massage ")
//@RequestMapping("/v1/massage")
//@JsonRootName("snapshot")
//@CrossOrigin(origins = "*", allowedHeaders = "*")
//public class MessageController {
//	@MessageMapping("/sendMessage")
//	@SendTo("/topic/group")
//	public Map<String, String> post(@Payload Map<String, String> message) {
//		message.put("timestamp", Long.toString(System.currentTimeMillis()));
//		return message;
//	}
//
//	@PostMapping("/history")
//	public List<Map<String, String>> getChatHistory() {
//		return new ArrayList<>();
//	}
//}
