//package com.migi.toeic.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.HashOperations;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//
//@Service
//public class TestRedisService {
//
//	private final String EMPLOYEE_CACHE = "EMPLOYEE";
//
//	@Autowired
//	RedisTemplate<String, Object> redisTemplate;
//	private HashOperations<String, String, Object> hashOperations;
//
//	// This annotation makes sure that the method needs to be executed after
//	// dependency injection is done to perform any initialization.
//	@PostConstruct
//	private void intializeHashOperations() {
//		hashOperations = redisTemplate.opsForHash();
//	}
//
//	// Save operation.
//	public void save(final Object obj, String id) {
//		hashOperations.put(EMPLOYEE_CACHE, id, obj);
//	}
//}