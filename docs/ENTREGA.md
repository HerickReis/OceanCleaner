# Ocean Cleaner — Entrega DevOps (Cidades ESG Inteligentes)

Projeto ESG ambiental (ODS 14): limpeza de áreas marítimas, voluntariado, relatórios de coleta.

Integrantes:
- Gabriel Borges Cedraz de Santana — ga.czsan@gmail.com
- Matheus de Oliveira Radeze — radezemat@outlook.com
- Sabrina Pires Gomes da Silva — sassadesabrina@gmail.com
- Herick Reis Nascimentos dos Santos — herickreis90.90@gmail.com

Repo privado. Compose escolhido (não Kubernetes).

Gerar PDF: `pandoc docs/ENTREGA.md -o docs/ENTREGA.pdf`

## 1. Pipeline: ferramenta, etapas, lógica

Ferramenta: GitHub Actions + GHCR + self-hosted runner Linux.

Arquivos: `.github/workflows/ci-cd.yml`, `.github/workflows/deploy.yml`, `deploy/deploy.sh`, `deploy/docker-compose.deploy.yml`.

Fluxo: push `master` -> Build/test matrix 4 svcs (`mvn verify`, JaCoCo 30%, Trivy HIGH,CRITICAL block) -> Docker build/push GHCR `:<sha>` + `:latest` -> Deploy staging auto (`:8081`/`:8762`) smoke `200` -> Aprovação manual -> Deploy produção (`:8080`/`:8761`).

PR roda só build/test. Imagens rastreadas por SHA em `/actuator/info` (`APP_VERSION`).

## 2. Docker: arquitetura, comandos, imagem

Arquitetura: `gateway:5051` -> `operacoes-ms`/`voluntarios-ms` (porta random via Eureka `eureka-sd:5050`) -> `oracle-db:1521`. Rede `oceancleaner-network`, volumes `oracle-data`, `oracle-backup`, env `.env`, healthcheck + `depends_on: service_healthy`.

`Dockerfile` por serviço: multi-stage `maven:3.9-eclipse-temurin-21-alpine` -> `eclipse-temurin:21-jre-alpine`, cache `pom.xml`, `USER spring`.

Comandos:
```bash
cp .env.example .env
docker compose up -d --build
docker compose ps; docker compose logs --tail=50
curl localhost:5051/actuator/info
docker compose down # -v apaga banco
```

Imagens GHCR: `oceancleaner-eureka-sd`, `oceancleaner-gateway`, `oceancleaner-operacoes-ms`, `oceancleaner-voluntarios-ms`.

## 3. Prints pipeline rodando

Ver `docs/prints/pipeline.png` (build, testes, deploy). Atualizar após run verde pós-commit.

![Pipeline](prints/pipeline.png)

Aprovação manual: `prints/aprovacao-producao.png`.

![Aprovação](prints/aprovacao-producao.png)

Histórico envs: `prints/environments.png`.

![Environments](prints/environments.png)

## 4. Prints staging e produção

`/actuator/info` com SHA: `prints/ambiente-info.png`.

![Ambientes](prints/ambiente-info.png)

Eureka 4 instâncias: `prints/eureka.png`.

![Eureka](prints/eureka.png)

Smoke `deploy.sh`: `GET /operacoes-ms/operacoes` + `GET /voluntarios-ms/voluntarios` = `200` via Gateway, senão logs + rollback `.deployed_tag`.

## 5. Desafios e soluções

| Desafio | Solução |
|---|---|
| Porta random + descoberta | Eureka + `lb()` Gateway, `stripPrefix(1)` |
| Oracle slow-start | healthcheck `healthcheck.sh`, `depends_on` |
| Feign 401 | `FeignConfig` BasicAuth `API_USER/API_PASSWORD` |
| Feign 500 genérico | `GlobalExceptionHandler` Feign -> `502` |
| Relatório duplicado | idempotência: só Feign se `statusAnterior != CONCLUIDA` |
| Conclusão sem dados | fail-fast `409` exige `idVoluntario/quantidade/tipo` |
| Delete órfão | bloqueia delete operação/voluntário/área com filhos `409` |
| Email duplicado | `findByEmail` + `409` |
| `status` case | `findByStatusIgnoreCase` |
| Repo privado sem Business | prod via `workflow_dispatch` manual, documentado |
| Sem runner | setup `~/actions-runner` sem espaços, Docker+10GB |

## 6. Checklist de entrega

- [x] Projeto compactado em .ZIP com estrutura organizada
- [x] Dockerfile funcional
- [x] docker-compose.yml ou arquivos Kubernetes (Compose escolhido)
- [x] Pipeline com etapas de build, teste e deploy
- [x] README.md com instruções e prints
- [ ] Documentação técnica com evidências (este PDF, refresh prints pós-run)
- [ ] Deploy realizado nos ambientes staging e produção (exige runner)
