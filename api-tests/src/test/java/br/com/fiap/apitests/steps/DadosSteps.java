package br.com.fiap.apitests.steps;

import br.com.fiap.apitests.Api;
import br.com.fiap.apitests.Contexto;
import io.cucumber.java.pt.Dado;
import io.restassured.response.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Pré-condições dos cenários ("Dado que existe ..."): criam os dados necessários pela própria API
 * e guardam os IDs como variáveis ({areaId}, {voluntarioId}, {operacaoId}, {relatorioId}).
 */
public class DadosSteps {

    private final Contexto contexto;
    private final Api api;

    public DadosSteps(Contexto contexto, Api api) {
        this.contexto = contexto;
        this.api = api;
    }

    @Dado("que estou autenticado como administrador")
    public void autenticado() {
        contexto.setAutenticado(true);
    }

    @Dado("que não estou autenticado")
    public void naoAutenticado() {
        contexto.setAutenticado(false);
    }

    @Dado("que existe uma área marítima cadastrada")
    public void existeArea() {
        criar("areaId", "/operacoes-ms/areas-maritimas", """
                {"nome": "Área de teste {unico}", "descricao": "Criada pelos testes de API",
                 "localizacao": "Santos - SP", "nivelPoluicao": "ALTO"}
                """);
    }

    @Dado("que existe um voluntário cadastrado")
    public void existeVoluntario() {
        criar("voluntarioId", "/voluntarios-ms/voluntarios", """
                {"nome": "Voluntário {unico}", "email": "voluntario.{unico}@teste.com",
                 "telefone": "13999990000", "especialidade": "Mergulho"}
                """);
    }

    @Dado("que existe uma operação {string} cadastrada nessa área")
    public void existeOperacao(String status) {
        criar("operacaoId", "/operacoes-ms/operacoes", """
                {"titulo": "Mutirão {unico}", "descricao": "Operação de teste",
                 "dataOperacao": "2026-12-10", "status": "%s", "idArea": {areaId}}
                """.formatted(status));
    }

    @Dado("que essa operação foi concluída pelo voluntário com {int} kg de {string}")
    public void operacaoConcluida(int quantidade, String tipo) {
        Response r = comoAdmin("PUT", "/operacoes-ms/operacoes/{operacaoId}", """
                {"titulo": "Mutirão {unico}", "dataOperacao": "2026-12-10", "status": "CONCLUIDA",
                 "idArea": {areaId}, "idVoluntario": {voluntarioId},
                 "quantidadeResiduos": %d, "tipoResiduo": "%s"}
                """.formatted(quantidade, tipo));
        assertEquals(200, r.statusCode(), "Falha ao concluir operação: " + r.asString());
    }

    @Dado("que existe um relatório de coleta dessa operação")
    public void existeRelatorio() {
        criar("relatorioId", "/voluntarios-ms/relatorios", """
                {"idOperacao": {operacaoId}, "idVoluntario": {voluntarioId},
                 "quantidadeResiduos": 35, "tipoResiduo": "Vidro", "observacoes": "Relatório de teste"}
                """);
    }

    private void criar(String variavel, String caminho, String corpo) {
        Response r = comoAdmin("POST", caminho, corpo);
        assertEquals(201, r.statusCode(), "Falha ao preparar dados em " + caminho + ": " + r.asString());
        contexto.guardar(variavel, r.jsonPath().get("id"));
    }

    /** Executa a preparação autenticado, sem mudar o estado de autenticação do cenário. */
    private Response comoAdmin(String metodo, String caminho, String corpo) {
        boolean antes = contexto.isAutenticado();
        contexto.setAutenticado(true);
        try {
            return api.enviar(metodo, contexto.resolver(caminho), contexto.resolver(corpo));
        } finally {
            contexto.setAutenticado(antes);
        }
    }
}
