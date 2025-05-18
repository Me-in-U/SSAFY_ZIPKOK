package com.ssafy.house;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class ZipkokApplication extends SpringBootServletInitializer {

	// WAR 배포 시 톰캣이 호출하는 메서드
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(ZipkokApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(ZipkokApplication.class, args);
	}

}
