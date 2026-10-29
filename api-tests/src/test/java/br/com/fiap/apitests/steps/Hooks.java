package br.com.fiap.apitests.steps;

import br.com.fiap.apitests.Ambiente;
import br.com.fiap.apitests.Api;
import br.com.fiap.apitests.Contexto;
import io.cucumber.java.After;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import io.restassured.filter.log.LogDetail;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class Hooks {

    private final Contexto contexto;
    private final Api api;

    public Hooks(Contexto contexto, Api api) {
        this.contexto = contexto;
        this.api = api;
    }

    /**
     * Aguarda o ambiente ficar pronto: os dois microsserviços precisam responder pelo Gateway
     * e aparecer no registro do Eureka. Evita falsos negativos logo após o docker compose up.
     */
    @BeforeAll
    public static void aguardarAmbiente() throws InterruptedException {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);
        System.out.println("Ambiente alvo: " + Ambiente.BASE_URL + " (Eureka: " + Ambiente.EUREKA_URL + ")");

        Instant limite = Instant.now().plus(Duration.ofSeconds(Ambiente.TIMEOUT_PRONTO_SEGUNDOS));
        boolean esperou = false;
        while (!ambientePronto()) {
            if (Instant.now().isAfter(limite)) {
                throw new IllegalStateException("Ambiente não ficou pronto em "
                        + Ambiente.TIMEOUT_PRONTO_SEGUNDOS + "s: " + Ambiente.BASE_URL);
            }
            esperou = true;
            System.out.println("Aguardando os serviços ficarem disponíveis...");
            Thread.sleep(5_000);
        }
        if (esperou) {
            // Ambiente acabou de subir: os clientes Eureka (Gateway e o Feign do operacoes-ms)
            // atualizam a lista de instâncias a cada 30s, então espera um ciclo completo.
            System.out.println("Serviços registrados; aguardando propagação do registro do Eureka (35s)...");
            Thread.sleep(35_000);
        }
    }

    private static boolean ambientePronto() {
        try {
            boolean gateway = RestAssured.given().baseUri(Ambiente.BASE_URL)
                    .get("/operacoes-ms/areas-maritimas").statusCode() == 200
                    && RestAssured.given().baseUri(Ambiente.BASE_URL)
                    .get("/voluntarios-ms/voluntarios").statusCode() == 200;
            List<String> registrados = RestAssured.given().baseUri(Ambiente.EUREKA_URL)
                    .accept("application/json").get("/eureka/apps")
                    .jsonPath().getList("applications.application.name");
            return gateway && registrados != null
                    && registrados.containsAll(List.of("OPERACOES-MS", "VOLUNTARIOS-MS"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Remove o que o cenário criou, deixando o banco como estava.
     * Relatórios gerados via Feign ao concluir uma operação não passam pelo POST do teste,
     * então são buscados pela operação e removidos antes dela.
     */
    @After
    public void limparDados(Scenario cenario) {
        for (String caminho : contexto.getCriados()) {
            if (caminho.startsWith("/operacoes-ms/operacoes/")) {
                String idOperacao = caminho.substring(caminho.lastIndexOf('/') + 1);
                List<Integer> relatorios = RestAssured.given().baseUri(Ambiente.BASE_URL)
                        .get("/voluntarios-ms/relatorios/operacao/" + idOperacao)
                        .jsonPath().getList("id", Integer.class);
                relatorios.forEach(id -> api.remover("/voluntarios-ms/relatorios/" + id));
            }
        }
        while (!contexto.getCriados().isEmpty()) {
            String caminho = contexto.getCriados().pop();
            int status = api.remover(caminho);
            if (status != 204 && status != 404) {
                System.out.println("Limpeza: DELETE " + caminho + " retornou " + status);
                cenario.log("Limpeza: DELETE " + caminho + " retornou " + status);
            }
        }
    }
}
