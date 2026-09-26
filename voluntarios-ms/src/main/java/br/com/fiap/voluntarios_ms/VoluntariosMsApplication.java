package br.com.fiap.voluntarios_ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class VoluntariosMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(VoluntariosMsApplication.class, args);
	}

}
