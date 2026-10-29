package br.com.fiap.apitests;

import io.restassured.response.Response;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Estado de um cenário, compartilhado entre as classes de steps (injeção via PicoContainer).
 * Uma instância nova é criada para cada cenário.
 */
public class Contexto {

    private static final Pattern VARIAVEL = Pattern.compile("\\{(\\w+)}");

    private final Map<String, String> variaveis = new HashMap<>();
    /** Recursos criados no cenário (caminho completo), removidos ao final em ordem inversa. */
    private final Deque<String> criados = new ArrayDeque<>();
    private boolean autenticado;
    private Response resposta;

    public Contexto() {
        // Sufixo único por cenário: evita colisão de e-mails e nomes entre execuções
        variaveis.put("unico", Long.toString(System.nanoTime(), 36));
    }

    /** Substitui {nome} pelas variáveis guardadas no cenário, ex.: /operacoes/{operacaoId}. */
    public String resolver(String texto) {
        Matcher m = VARIAVEL.matcher(texto);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String nome = m.group(1);
            if (!variaveis.containsKey(nome)) {
                throw new IllegalStateException("Variável não definida no cenário: {" + nome + "}");
            }
            m.appendReplacement(sb, Matcher.quoteReplacement(variaveis.get(nome)));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public void guardar(String nome, Object valor) {
        variaveis.put(nome, String.valueOf(valor));
    }

    public String variavel(String nome) {
        return variaveis.get(nome);
    }

    public void registrarCriado(String caminho) {
        criados.push(caminho);
    }

    public Deque<String> getCriados() {
        return criados;
    }

    public boolean isAutenticado() {
        return autenticado;
    }

    public void setAutenticado(boolean autenticado) {
        this.autenticado = autenticado;
    }

    public Response getResposta() {
        return resposta;
    }

    public void setResposta(Response resposta) {
        this.resposta = resposta;
    }
}
