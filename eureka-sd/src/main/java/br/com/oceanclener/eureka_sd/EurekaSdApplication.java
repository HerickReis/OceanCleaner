package br.com.oceanclener.eureka_sd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaSdApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaSdApplication.class, args);
	}

}
