package br.com.fiap.apitests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Cliente HTTP dos testes: todas as chamadas passam pelo Gateway (BASE_URL).
 * Recursos criados com sucesso (201) são registrados no contexto para limpeza no fim do cenário.
 */
public class Api {

    private final Contexto contexto;

    public Api(Contexto contexto) {
        this.contexto = contexto;
    }

    public Response enviar(String metodo, String caminho, String corpo) {
        RequestSpecification req = RestAssured.given()
                .baseUri(Ambiente.BASE_URL)
                .accept(ContentType.JSON);
        if (contexto.isAutenticado()) {
            req.auth().preemptive().basic(Ambiente.API_USER, Ambiente.API_PASSWORD);
        }
        if (corpo != null) {
            req.contentType(ContentType.JSON).body(corpo);
        }
        Response resposta = req.request(metodo, caminho);
        contexto.setResposta(resposta);

        if ("POST".equals(metodo) && resposta.statusCode() == 201) {
            Object id = resposta.jsonPath().get("id");
            if (id != null) {
                contexto.registrarCriado(caminho + "/" + id);
            }
        }
        return resposta;
    }

    /** DELETE autenticado usado na limpeza, sem alterar a resposta do cenário. */
    public int remover(String caminho) {
        return RestAssured.given()
                .baseUri(Ambiente.BASE_URL)
                .auth().preemptive().basic(Ambiente.API_USER, Ambiente.API_PASSWORD)
                .delete(caminho)
                .statusCode();
    }
}
