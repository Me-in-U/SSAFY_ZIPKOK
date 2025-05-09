package com.ssafy.house;

import org.springframework.ai.autoconfigure.vectorstore.redis.RedisVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = RedisVectorStoreAutoConfiguration.class)
public class ZipkokApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZipkokApplication.class, args);
	}

}
