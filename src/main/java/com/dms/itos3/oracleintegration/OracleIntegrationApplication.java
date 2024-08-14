package com.dms.itos3.oracleintegration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OracleIntegrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(OracleIntegrationApplication.class, args);
	}

}
