# Testes dos endpoints (Postman / Insomnia)

Coleção com **40 requisições** que exercitam todos os endpoints do Ocean Cleaner pelo Gateway, incluindo os casos de erro (400, 401, 404, 409) e o fluxo entre serviços: concluir uma operação gera o relatório de coleta no `voluntarios-ms`.

| Arquivo | Conteúdo |
|---|---|
| `OceanCleaner.postman_collection.json` | A coleção, com testes automáticos de status e conteúdo em cada requisição |
| `OceanCleaner-local.postman_environment.json` | `baseUrl` = `http://localhost:5051` (docker compose local) |
| `OceanCleaner-staging.postman_environment.json` | `baseUrl` = `http://localhost:8081` |
| `OceanCleaner-producao.postman_environment.json` | `baseUrl` = `http://localhost:8080` |

## Postman

1. *Import* → selecione a coleção e os arquivos de ambiente.
2. Escolha o ambiente no canto superior direito e preencha **`apiPassword`** com o valor de `API_PASSWORD` daquele ambiente. A senha não fica salva nos arquivos.
3. Clique com o botão direito na coleção → *Run collection* → *Run*. Rode **todas as pastas, na ordem**: cada requisição guarda os IDs criados para as seguintes.

A última pasta (**7. Limpeza**) apaga tudo o que foi criado, então a coleção pode ser executada várias vezes. Se uma execução parar no meio, os registros criados até ali ficam no banco.

## Insomnia

*Create* → *Import* → selecione `OceanCleaner.postman_collection.json`. O Insomnia importa as requisições e os corpos, mas **não executa os scripts de teste do Postman**: os IDs (`areaId`, `voluntarioId`, `operacaoId`, `relatorioId`) precisam ser preenchidos manualmente nas variáveis, e os status devem ser conferidos a olho.

## Linha de comando (Newman)

Com Node.js instalado:

```bash
npx newman run docs/postman/OceanCleaner.postman_collection.json \
  -e docs/postman/OceanCleaner-staging.postman_environment.json \
  --env-var apiPassword=SUA_SENHA
```

## O que é testado

| Pasta | Cobertura |
|---|---|
| 0. Saúde do ambiente | `/actuator/info` (ambiente e versão) e `/actuator/health` |
| 1. Áreas marítimas | CRUD completo, validação (400) e área inexistente (404) |
| 2. Voluntários | CRUD, e-mail duplicado (409), validação (400) e escrita sem login (401) |
| 3. Operações | Cadastro, listagens, filtro por status sem diferenciar maiúsculas, 401 e 404 |
| 4. Conclusão e relatório | Conclusão sem dados (409), conclusão gerando relatório via Feign e ausência de relatório duplicado |
| 5. Relatórios de coleta | Consultas por id, operação e voluntário; cadastro manual e voluntário inexistente (404) |
| 6. Exclusões bloqueadas | Área com operação, operação com relatório e voluntário com relatório (409) |
| 7. Limpeza | Exclusões na ordem permitida (204) e confirmação (404) |
