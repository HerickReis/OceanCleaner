package br.com.fiap.operacoes_ms;

import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor(
            @Value("${api.security.username}") String username,
            @Value("${api.security.password}") String password) {
        return new BasicAuthRequestInterceptor(username, password);
    }
}