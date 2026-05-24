package com.crhistianm.springboot.gallo.springboot_gallo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = RedisAutoConfiguration.class)
@EnableCaching
public class SpringbootGalloApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootGalloApplication.class, args);
	}

}
