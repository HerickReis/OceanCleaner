#!/usr/bin/env bash
# Executa os testes de API (BDD + contrato) contra o ambiente do Ocean Cleaner.
#
# Uso:
#   ./api-tests/executar.sh             # testa o ambiente já no ar (padrão: http://localhost:5051)
#   ./api-tests/executar.sh --subir     # sobe o docker compose local antes de testar
#   ./api-tests/executar.sh --subir --derrubar   # ... e derruba tudo (inclusive o banco) no fim
#
# Variáveis opcionais: BASE_URL, EUREKA_URL, API_USER, API_PASSWORD, READY_TIMEOUT_SECONDS.
# Se API_USER/API_PASSWORD não forem informados, são lidos do .env da raiz do projeto.
# Argumentos extras vão para o Maven, ex.: ./api-tests/executar.sh -Dcucumber.filter.tags=@principal
set -euo pipefail

RAIZ="$(cd "$(dirname "$0")/.." && pwd)"
SUBIR=false
DERRUBAR=false
MAVEN_ARGS=()
for arg in "$@"; do
  case "$arg" in
    --subir) SUBIR=true ;;
    --derrubar) DERRUBAR=true ;;
    *) MAVEN_ARGS+=("$arg") ;;
  esac
done

if [[ -f "$RAIZ/.env" ]]; then
  : "${API_USER:=$(grep -E '^API_USER=' "$RAIZ/.env" | cut -d= -f2-)}"
  : "${API_PASSWORD:=$(grep -E '^API_PASSWORD=' "$RAIZ/.env" | cut -d= -f2-)}"
fi
export API_USER API_PASSWORD

if [[ -z "${API_PASSWORD:-}" ]]; then
  echo "API_PASSWORD não definido (crie o .env a partir do .env.example ou exporte a variável)." >&2
  exit 1
fi

if $DERRUBAR; then
  trap 'docker compose -f "$RAIZ/docker-compose.yaml" --project-directory "$RAIZ" down -v' EXIT
fi

if $SUBIR; then
  docker compose -f "$RAIZ/docker-compose.yaml" --project-directory "$RAIZ" up -d --build
fi

cd "$RAIZ/api-tests"
sh ./mvnw -B test "${MAVEN_ARGS[@]}"
echo "Relatório: $RAIZ/api-tests/target/cucumber-report.html"
