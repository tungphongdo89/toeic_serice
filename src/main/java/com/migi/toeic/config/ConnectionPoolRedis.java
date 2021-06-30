//package com.migi.toeic.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisPoolConfig;
//import redis.clients.jedis.JedisSentinelPool;
//import redis.clients.jedis.exceptions.JedisConnectionException;
//
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.util.Date;
//import java.util.HashSet;
//import java.util.Set;
//
//
//@Configuration
//@Slf4j
//public class ConnectionPoolRedis {
////	private static Logger logger = log.getLogger(ConnectionPoolRedis.class);
////	private static final Logger log = LogManager.getLogger(ConnectionPoolRedis.class);
//	@Autowired
//	Environment env;
//
//	@Value("${redis.sentinelHost}")
//	private String redisHost;
//
//	@Value("${redis.port}")
//	private int redisPort;
//
//	@Value("${redis.maxTotal}")
//	private int maxTotalGlobal;
//
//	@Value("${redis.master}")
//	private String master;
//
//	@Value("${redis.password}")
//	private String password;
//
//	@Value("${redis.timeOut}")
//	private int timeOut;
//
//
//	private static ConnectionPoolRedis instance;
//	private JedisSentinelPool redisPool;
//	String[] sentinelHost;
//	int TIME_OUT;
//
//	public String[] getSentinelHost() {
//		return sentinelHost;
//	}
//
//	public void setSentinelHost(String[] sentinelHost) {
//		this.sentinelHost = sentinelHost;
//	}
//
////	public ConnectionPoolRedis() {
////		if (redisPool == null) {
////			TIME_OUT = timeOut;
////			try {
////				initConnectionPool();
////			} catch (Exception e) {
////				log.error(e.getMessage(), e);
////			}
////		}
////	}
//
//	public JedisSentinelPool getRedisPool() {
//		return redisPool;
//	}
//
//	public static synchronized ConnectionPoolRedis getInstance() throws Exception {
//		if (instance == null) {
//			instance = new ConnectionPoolRedis();
//		}
//		return instance;
//	}
//
//	private void initConnectionPool() throws Exception {
//		try {
//			log.error("a du da vao day =========> " + redisHost);
//			JedisPoolConfig config = new JedisPoolConfig();
////			int maxTotal = maxTotalGlobal;
//			config.setMaxTotal(maxTotalGlobal);
////            config.setBlockWhenExhausted(true);
//			config.setMaxWaitMillis(30000);
//			config.setTestOnBorrow(true);
//			config.setTestOnCreate(true);
//			config.setTestWhileIdle(true);
//			setSentinelHost(redisHost.split(","));
//			Set<String> sentinels = new HashSet<>();
//			String[] sentinelHost = getSentinelHost();
//			for (String host : sentinelHost) {
//				sentinels.add(host.trim());
//			}
////			String master = env.getProperty("redis.master", "huynv");
////			String password = env.getProperty("redis.password");
//			redisPool = new JedisSentinelPool(master, sentinels, config, TIME_OUT, password);
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//			throw new Exception("Create connectionPoolRedis failed");
//		}
//	}
//
//	public static String getStackTraceAsString(Exception ex) {
//		StringWriter sw = new StringWriter();
//		ex.printStackTrace(new PrintWriter(sw));
//		return sw.toString();
//	}
//
//	public synchronized Jedis getRedis(int redisDB) throws Exception {
//		Jedis jedis = null;
//		try {
//			System.out.println("Pool jedis NumActive: " + getInstance().getRedisPool().getNumActive() + ", NumIdle: " + getInstance().getRedisPool().getNumIdle() + ", NumWaiters: " + getInstance().getRedisPool().getNumWaiters());
//			Date startTime = new Date();
//			jedis = redisPool.getResource();
//
//			Date endTime = new Date();
//			long totalTime = endTime.getTime() - startTime.getTime();
//			System.out.println("Total time: " + totalTime);
//			if (!jedis.isConnected()) {
//				jedis.connect();
//			}
//			jedis.select(redisDB);
//		} catch (JedisConnectionException e) {
//			log.error(e.getMessage(), e);
//			if (jedis != null) {
//				jedis.close();
//				jedis.connect();
//				jedis.select(redisDB);
//				return jedis;
//			} else {
//				return getRedis(redisDB);
//			}
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//			String msgError = getStackTraceAsString(e);
//			if (msgError.contains("Timeout waiting for idle object")) {
//				log.info("============= DESTROY POOL REDIS ==============");
//				getInstance().destroy();
//				log.info("============= INNIT POOL REDIS ==============");
//				getInstance().initConnectionPool();
//				log.info("============= END POOL REDIS ==============");
//				log.info("Begin get redis from new pool");
//				jedis = getRedis(redisDB);
//				log.info("End get redis from new pool");
//				return jedis;
//			} else {
//				throw new Exception("Get connection redis failed");
//			}
//		}
//		return jedis;
//	}
//
//	public void closeJedis(Jedis jedis) {
//		try {
//			if (jedis == null) return;
//			if (redisPool != null) {
//				redisPool.close();
//			}
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//		}
//	}
//
//	public void destroy() {
//		try {
//			if (redisPool != null) {
//				redisPool.destroy();
//			}
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//		}
//	}
//
//	@Bean
//	public void initRedis() throws Exception {
//		if (redisPool == null) {
//			TIME_OUT = timeOut;
//			try {
//				initConnectionPool();
//			} catch (Exception e) {
//				log.error(e.getMessage(), e);
//			}
//		}
//		getInstance().getRedisPool();
//	}
//
//	public static void main(String[] args) throws Exception {
//		getInstance().getRedisPool();
//	}
//}
