package br.com.fiap.apitests.steps;

import br.com.fiap.apitests.Ambiente;
import br.com.fiap.apitests.Api;
import br.com.fiap.apitests.Contexto;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Steps genéricos: chamadas HTTP e validações de status, corpo JSON e contrato. */
public class ApiSteps {

    private final Contexto contexto;
    private final Api api;

    public ApiSteps(Contexto contexto, Api api) {
        this.contexto = contexto;
        this.api = api;
    }

    // ------------------------------------------------------------------ requisições

    @Quando("envio um {word} para {string}")
    public void envioSemCorpo(String metodo, String caminho) {
        api.enviar(metodo, contexto.resolver(caminho), null);
    }

    @Quando("envio um {word} para {string} com o corpo:")
    public void envioComCorpo(String metodo, String caminho, String corpo) {
        api.enviar(metodo, contexto.resolver(caminho), contexto.resolver(corpo));
    }

    @Quando("consulto os serviços registrados no Eureka")
    public void consultoEureka() {
        contexto.setResposta(RestAssured.given()
                .baseUri(Ambiente.EUREKA_URL)
                .accept(ContentType.JSON)
                .get("/eureka/apps"));
    }

    // ------------------------------------------------------------------ status code

    @Então("o status da resposta deve ser {int}")
    public void statusDeveSer(int esperado) {
        Response r = contexto.getResposta();
        assertEquals(esperado, r.statusCode(), "Status inesperado. Corpo: " + r.asString());
    }

    // ------------------------------------------------------------------ contrato (JSON Schema)

    @Então("a resposta deve respeitar o contrato {string}")
    public void respeitaContrato(String schema) {
        contexto.getResposta().then().assertThat()
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("schemas/" + schema));
    }

    // ------------------------------------------------------------------ corpo (JSON)

    @Então("a resposta deve conter:")
    public void respostaDeveConter(DataTable tabela) {
        JsonPath json = json();
        for (Map.Entry<String, String> linha : tabela.asMap(String.class, String.class).entrySet()) {
            Object atual = json.get(linha.getKey());
            assertEquals(contexto.resolver(linha.getValue()), String.valueOf(atual),
                    "Campo '" + linha.getKey() + "' diferente. Corpo: " + json.prettify());
        }
    }

    @Então("o campo {string} da resposta deve conter o texto {string}")
    public void campoDeveConterTexto(String campo, String trecho) {
        String atual = json().getString(campo);
        assertNotNull(atual, "Campo '" + campo + "' ausente na resposta");
        assertTrue(atual.contains(contexto.resolver(trecho)),
                "Campo '" + campo + "' = \"" + atual + "\" não contém \"" + trecho + "\"");
    }

    @Então("guardo o campo {string} da resposta como {string}")
    public void guardoCampo(String campo, String variavel) {
        Object valor = json().get(campo);
        assertNotNull(valor, "Campo '" + campo + "' ausente na resposta");
        contexto.guardar(variavel, valor);
    }

    @Então("a lista da resposta deve conter o item com id {string}")
    public void listaContemId(String id) {
        List<Object> ids = json().getList("id");
        String esperado = contexto.resolver(id);
        assertTrue(ids.stream().map(String::valueOf).anyMatch(esperado::equals),
                "id " + esperado + " não encontrado na lista " + ids);
    }

    @Então("a lista da resposta não deve conter o item com id {string}")
    public void listaNaoContemId(String id) {
        List<Object> ids = json().getList("id");
        String esperado = contexto.resolver(id);
        assertFalse(ids.stream().map(String::valueOf).anyMatch(esperado::equals),
                "id " + esperado + " não deveria estar na lista " + ids);
    }

    @Então("a lista da resposta deve ter {int} item/itens")
    public void listaTamanho(int tamanho) {
        assertEquals(tamanho, json().getList("$").size(), "Tamanho da lista. Corpo: " + json().prettify());
    }

    @Então("todos os itens da lista devem ter {string} igual a {string}")
    public void todosItensCom(String campo, String valor) {
        List<Object> valores = json().getList(campo);
        assertFalse(valores.isEmpty(), "Lista vazia");
        valores.forEach(v -> assertEquals(contexto.resolver(valor), String.valueOf(v)));
    }

    @Então("o Eureka deve listar o serviço {string}")
    public void eurekaListaServico(String nome) {
        List<String> nomes = json().getList("applications.application.name");
        assertTrue(nomes.contains(nome), nome + " não registrado no Eureka. Registrados: " + nomes);
        List<String> status = json().getList("applications.application.find { it.name == '" + nome + "' }.instance.status");
        assertTrue(status.contains("UP"), nome + " sem instância UP: " + status);
    }

    private JsonPath json() {
        return contexto.getResposta().jsonPath();
    }
}
