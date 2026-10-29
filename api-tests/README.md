# Testes de API — BDD + contrato

Testes automatizados **caixa-preta** de todas as APIs do Ocean Cleaner. Os cenários são escritos em **Gherkin (português)** e executados com **Cucumber + Rest Assured** contra o **Gateway**, ou seja, cada requisição passa pelo mesmo caminho de um cliente real: Gateway → Eureka → microsserviço → Oracle (e, na conclusão de uma operação, `operacoes-ms` → Feign → `voluntarios-ms`).

Cada cenário valida:

| Validação | Como |
|---|---|
| **Status code** | `Então o status da resposta deve ser 201` |
| **Corpo da resposta (JSON)** | `E a resposta deve conter:` + tabela `campo \| valor` (aceita caminhos como `[0].tipoResiduo`) |
| **Contrato (JSON Schema)** | `E a resposta deve respeitar o contrato "operacao.json"` — schemas em [`src/test/resources/schemas`](src/test/resources/schemas) |
| **Comunicação entre serviços** | Registro no Eureka, roteamento do Gateway e chamadas Feign `operacoes-ms` → `voluntarios-ms` (sucesso e falha) |

## Como executar

**Pré-requisitos:** Docker e Java 21 (o Maven vem pelo wrapper `mvnw`).

```bash
cp .env.example .env                       # uma vez, na raiz do projeto; preencha as senhas
./api-tests/executar.sh --subir            # sobe o docker compose e roda os testes
```

| Comando | O que faz |
|---|---|
| `./api-tests/executar.sh` | Roda contra um ambiente já no ar (padrão `http://localhost:5051`) |
| `./api-tests/executar.sh --subir` | Sobe o `docker compose` local antes |
| `./api-tests/executar.sh --subir --derrubar` | Sobe, testa e derruba tudo (inclusive o volume do banco) — é o que o CI faz |
| `./api-tests/executar.sh -Dcucumber.filter.tags=@principal` | Só os cenários principais (veja as tags abaixo) |
| `BASE_URL=http://localhost:8081 EUREKA_URL=http://localhost:8762 API_PASSWORD=*** ./api-tests/executar.sh` | Contra o **staging** |

Sem o script, de dentro de `api-tests/`: `./mvnw test -DAPI_PASSWORD=...`.

Os testes esperam o ambiente ficar pronto sozinhos (até `READY_TIMEOUT_SECONDS`, padrão 300 s): os dois microsserviços precisam responder pelo Gateway e estar registrados no Eureka. Então dá para rodar logo depois do `docker compose up`.

### Variáveis

| Variável | Padrão | Uso |
|---|---|---|
| `BASE_URL` | `http://localhost:5051` | Gateway do ambiente alvo |
| `EUREKA_URL` | `http://localhost:5050` | Eureka do ambiente alvo |
| `API_USER` / `API_PASSWORD` | lidos do `.env` da raiz | HTTP Basic para POST/PUT/DELETE |
| `READY_TIMEOUT_SECONDS` | `300` | Espera máxima pelo ambiente |

Podem ser passadas como variável de ambiente ou como `-DNOME=valor` no Maven.

### Relatórios

Gerados em `api-tests/target/`:
- `cucumber-report.html` — relatório navegável, cenário a cenário (no CI vai no artifact **api-tests-report**);
- `cucumber.json` e `cucumber.xml` (JUnit) — para ferramentas de CI.

Em caso de falha, a requisição e a resposta completas aparecem no log.

### Dados de teste

Cada cenário cria os próprios dados pela API (com sufixo único para não colidir e-mails/nomes) e **remove tudo ao final** (hook `@After`), inclusive os relatórios gerados pelo Feign. A suíte pode rodar repetidas vezes, inclusive no staging, sem deixar lixo no banco.

## Cenários

45 cenários em 5 funcionalidades ([`src/test/resources/features`](src/test/resources/features)):

| Feature | Positivos | Negativos |
|---|---|---|
| `01_infraestrutura` | Gateway `health`/`info`; serviços registrados no Eureka | Rota inexistente no Gateway (404) |
| `02_areas_maritimas` | Cadastrar + buscar, listar, atualizar, remover | Campos obrigatórios (400 ×3), inexistente (404), sem login (401), área com operação (409) |
| `03_voluntarios` | Cadastrar + buscar, listar, atualizar, remover | E-mail duplicado (409), dados inválidos (400 ×3), inexistente (404), sem login (401), voluntário com relatório (409) |
| `04_operacoes` | Planejar + buscar, listar, filtrar por status, atualizar, remover, **concluir → relatório criado no voluntarios-ms** | Conclusão sem dados (409), voluntário inexistente na conclusão (**502**, falha do Feign), operação com relatório (409), área inexistente (404), obrigatórios (400), inexistente (404), sem login (401) |
| `05_relatorios` | Registrar + buscar, listar, por operação e por voluntário, remover, operação sem coleta (lista vazia) | Voluntário inexistente (404), obrigatórios (400), sem login (401) |

### Cobertura de endpoints

| Serviço | Endpoint | Testado em |
|---|---|---|
| gateway | `GET /actuator/health`, `GET /actuator/info` | 01 |
| eureka-sd | `GET /eureka/apps` | 01 |
| operacoes-ms | `POST/GET /areas-maritimas`, `GET/PUT/DELETE /areas-maritimas/{id}` | 02 |
| operacoes-ms | `POST/GET /operacoes`, `GET/PUT/DELETE /operacoes/{id}`, `GET /operacoes/status/{status}` | 04 |
| voluntarios-ms | `POST/GET /voluntarios`, `GET/PUT/DELETE /voluntarios/{id}` | 03 |
| voluntarios-ms | `POST/GET /relatorios`, `GET/DELETE /relatorios/{id}`, `GET /relatorios/operacao/{id}`, `GET /relatorios/voluntario/{id}` | 05 (e 04, via Feign) |

### Tags

| Tag | Seleciona |
|---|---|
| `@principal` | Os cenários centrais do negócio (felizes e de falha) |
| `@integracao` | Comunicação `operacoes-ms` → `voluntarios-ms` (Feign) |
| `@infraestrutura` | Gateway e Eureka |
| `@operacoes-ms`, `@voluntarios-ms` | Por microsserviço |
| `@areas-maritimas`, `@operacoes`, `@voluntarios`, `@relatorios` | Por recurso |

### Exemplo

```gherkin
@principal @integracao
Cenário: Concluir uma operação gera o relatório de coleta no voluntarios-ms
  Dado que existe um voluntário cadastrado
  E que existe uma operação "EM_ANDAMENTO" cadastrada nessa área
  Quando envio um PUT para "/operacoes-ms/operacoes/{operacaoId}" com o corpo:
    """
    {"titulo": "Mutirão {unico}", "dataOperacao": "2026-12-10", "status": "CONCLUIDA", "idArea": {areaId},
     "idVoluntario": {voluntarioId}, "quantidadeResiduos": 120, "tipoResiduo": "Plástico", ...}
    """
  Então o status da resposta deve ser 200
  E a resposta deve respeitar o contrato "operacao.json"
  Quando envio um GET para "/voluntarios-ms/relatorios/operacao/{operacaoId}"
  Então o status da resposta deve ser 200
  E a resposta deve respeitar o contrato "relatorio-lista.json"
  E a lista da resposta deve ter 1 item
  E a resposta deve conter:
    | [0].quantidadeResiduos | 120      |
    | [0].tipoResiduo        | Plástico |
```

`{areaId}`, `{operacaoId}` etc. são IDs guardados durante o cenário; `{unico}` é um sufixo único por cenário.

## Contratos (JSON Schema)

| Schema | Resposta |
|---|---|
| `area-maritima.json` / `area-maritima-lista.json` | `AreaMaritimaExibicaoDto` |
| `operacao.json` / `operacao-lista.json` | `OperacaoExibicaoDto` |
| `voluntario.json` / `voluntario-lista.json` | `VoluntarioExibicaoDto` |
| `relatorio.json` / `relatorio-lista.json` | `RelatorioColetaExibicaoDto` |
| `erro.json` | Erros de negócio `{"erro": "..."}` (404, 409, 502) |
| `erro-validacao.json` | Erros de validação `{"campo": "mensagem"}` (400) |
| `health.json`, `info.json`, `eureka-apps.json` | Actuator do Gateway e registro do Eureka |

Os schemas das entidades usam `additionalProperties: false` e listam todos os campos como obrigatórios: **adicionar, remover ou renomear um campo da resposta quebra o contrato** e o teste falha — o objetivo é detectar mudanças incompatíveis antes que cheguem a quem consome a API. Datas são validadas no formato `AAAA-MM-DD`; campos opcionais aceitam `null`.

Ao mudar um DTO de propósito, atualize o schema correspondente no mesmo commit.

## Estrutura

```
api-tests/
├── executar.sh                       # execução local / CI
├── pom.xml                           # Cucumber 7, Rest Assured 5, JUnit 5
└── src/test/
    ├── java/br/com/fiap/apitests/
    │   ├── RunCucumberTest.java      # runner (JUnit Platform Suite)
    │   ├── Ambiente.java             # BASE_URL, EUREKA_URL, credenciais
    │   ├── Api.java                  # cliente HTTP (Rest Assured)
    │   ├── Contexto.java             # estado do cenário e variáveis {xxx}
    │   └── steps/
    │       ├── ApiSteps.java         # requisições e validações (status, corpo, contrato)
    │       ├── DadosSteps.java       # pré-condições "Dado que existe ..."
    │       └── Hooks.java            # espera o ambiente e limpa os dados
    └── resources/
        ├── features/*.feature        # cenários em Gherkin
        └── schemas/*.json            # contratos (JSON Schema draft-04)
```
