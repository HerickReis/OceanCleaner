package br.com.fiap.apitests;

/**
 * Configuração do ambiente alvo. Cada valor pode vir de -Dpropriedade (Maven)
 * ou de variável de ambiente com o mesmo nome; senão usa o padrão do docker compose local.
 */
public final class Ambiente {

    public static final String BASE_URL = valor("BASE_URL", "http://localhost:5051");
    public static final String EUREKA_URL = valor("EUREKA_URL", "http://localhost:5050");
    public static final String API_USER = valor("API_USER", "admin");
    public static final String API_PASSWORD = valor("API_PASSWORD", "");
    public static final int TIMEOUT_PRONTO_SEGUNDOS = Integer.parseInt(valor("READY_TIMEOUT_SECONDS", "300"));

    private Ambiente() {
    }

    private static String valor(String nome, String padrao) {
        String v = System.getProperty(nome);
        if (v == null || v.isBlank()) {
            v = System.getenv(nome);
        }
        return (v == null || v.isBlank()) ? padrao : v;
    }
}
