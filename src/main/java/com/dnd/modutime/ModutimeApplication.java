package com.dnd.modutime;

import com.dnd.modutime.core.infrastructure.kakao.config.KakaoClientConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(KakaoClientConfiguration.class)
public class ModutimeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModutimeApplication.class, args);
	}

}
