package com.portfolio.docqa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DocqaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocqaApplication.class, args);
	}

}
