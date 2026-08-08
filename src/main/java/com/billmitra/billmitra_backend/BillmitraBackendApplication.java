package com.billmitra.billmitra_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.billmitra.billmitra_backend")
public class BillmitraBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(BillmitraBackendApplication.class, args);
	}
}