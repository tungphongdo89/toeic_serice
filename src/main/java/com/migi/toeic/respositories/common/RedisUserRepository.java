//package com.migi.toeic.respositories.common;
//
//import org.springframework.data.redis.core.HashOperations;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public class RedisUserRepository {
//
//	private HashOperations hashOperations;
//
//	private RedisTemplate redisTemplate;
//
//	public RedisUserRepository(RedisTemplate redisTemplate){
//		this.redisTemplate = redisTemplate;
//		this.hashOperations = this.redisTemplate.opsForHash();
//	}
//
//	public void save(Class cls,String key,){
//		hashOperations.put(cls.getName(), key, user);
//	}
//	public List findAll(){
//		return hashOperations.values("USER");
//	}
//
//	public User findById(String id){
//		return (User) hashOperations.get("USER", id);
//	}
//
//	public void update(User user){
//		save(user);
//	}
//
//	public void delete(String id){
//		hashOperations.delete("USER", id);
//	}
//
//}
