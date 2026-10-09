package br.com.oceanclener.eureka_sd;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EurekaSdApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void applicationStarts() {
		EurekaSdApplication.main(new String[]{"--server.port=0"});
	}

}
