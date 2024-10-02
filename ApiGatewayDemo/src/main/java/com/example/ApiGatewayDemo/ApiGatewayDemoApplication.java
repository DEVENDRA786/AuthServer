package com.example.ApiGatewayDemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;



@SpringBootApplication
@ComponentScan("com.example")
public class ApiGatewayDemoApplication {

	public static void main(String[] args) {

		SpringApplication.run(ApiGatewayDemoApplication.class, args);
	}
}