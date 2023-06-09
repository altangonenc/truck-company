package com.fiseq.truckcompany;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TruckCompanyApplication {

	public static void main(String[] args) {
		SpringApplication.run(TruckCompanyApplication.class, args);
	}

}
