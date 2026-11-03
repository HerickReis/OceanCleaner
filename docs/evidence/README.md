# Evidências de execução — suíte api-tests

Este diretório contém as evidências da suíte de testes de API já existente no projeto (`api-tests`), sem alterações nos testes.

## Origem

- Workflow: **CI/CD - Ocean Cleaner**
- Run: **41266518830**
- Job: **Testes de API (BDD + contrato)** — 124730480825
- Commit: **73ea4dd** (`master`)
- Resultado: **45/45 cenários, 0 falhas**
- Artifact: **api-tests-report**

## Arquivos

- `execucao-ci.png`: print do log real do job com `Tests run: 45` e `BUILD SUCCESS`.
- `report-resumo.png`: relatório HTML com 45 PASSED / 100%.
- `report-operacoes.png`: feature de operações expandida.
- `report-infra.png`: feature de infraestrutura expandida.
- `report-integracao.png`: cenário de conclusão com relatório via Feign.
- `config-*.png`: prints de POM, script, feature, schema, CI e README da suíte.
- `logs/api-tests-ci.txt`: trecho do log real do CI.
- `logs/api-tests-resultado.txt`: resumo do XML JUnit do Cucumber.
- `../EVIDENCIAS-TESTES.pdf`: documento consolidado (30 páginas).
- `../EVIDENCIAS-TESTES.pptx`: apresentação (12 slides).

## Reprodução

```bash
cp .env.example .env
./api-tests/executar.sh --subir --derrubar
```

A suíte exige Docker e Oracle; as evidências acima vêm da execução no GitHub Actions.
