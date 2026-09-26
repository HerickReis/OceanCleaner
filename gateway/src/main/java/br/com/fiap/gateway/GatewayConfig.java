package br.com.fiap.gateway;

import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

// Foi o jeito :(
@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> operacoesRoute() {
        return GatewayRouterFunctions.route("operacoes-ms")
                .route(RequestPredicates.path("/operacoes-ms/**"),
                        HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("operacoes-ms"))
                .before(BeforeFilterFunctions.stripPrefix(1))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> voluntariosRoute() {
        return GatewayRouterFunctions.route("voluntarios-ms")
                .route(RequestPredicates.path("/voluntarios-ms/**"),
                        HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("voluntarios-ms"))
                .before(BeforeFilterFunctions.stripPrefix(1))
                .build();
    }
}