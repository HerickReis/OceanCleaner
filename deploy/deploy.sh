#!/usr/bin/env bash
# Executado pelo pipeline, no self-hosted runner, para subir/atualizar um ambiente.
# Uso: ./deploy.sh <staging|production>
# Espera encontrar, na mesma pasta, o docker-compose.deploy.yml e o .env do ambiente.
set -euo pipefail

AMBIENTE="${1:?Informe o ambiente: staging ou production}"
cd "$(dirname "$0")"

# Carrega o .env para usar GATEWAY_PORT no smoke test
set -a; source .env; set +a

PROJETO="oceancleaner-${AMBIENTE}"
COMPOSE="docker compose -p ${PROJETO} -f docker-compose.deploy.yml --env-file .env"

echo ">> [${AMBIENTE}] Baixando imagens (tag ${IMAGE_TAG})..."
$COMPOSE pull

echo ">> [${AMBIENTE}] Subindo containers..."
$COMPOSE up -d --remove-orphans

# Smoke test: o deploy só é considerado OK quando o Gateway responde
# e os dois microsserviços estão registrados no Eureka e acessíveis por ele.
echo ">> [${AMBIENTE}] Aguardando a aplicação responder na porta ${GATEWAY_PORT}..."
for i in $(seq 1 60); do
  op=$(curl -s -o /dev/null -w '%{http_code}' "http://localhost:${GATEWAY_PORT}/operacoes-ms/operacoes" || true)
  vol=$(curl -s -o /dev/null -w '%{http_code}' "http://localhost:${GATEWAY_PORT}/voluntarios-ms/voluntarios" || true)
  if [ "$op" = "200" ] && [ "$vol" = "200" ]; then
    echo ">> [${AMBIENTE}] Deploy OK!"
    curl -s "http://localhost:${GATEWAY_PORT}/actuator/info"; echo
    $COMPOSE ps
    # Remove imagens antigas para não encher o disco
    docker image prune -f > /dev/null
    exit 0
  fi
  sleep 10
done

echo ">> [${AMBIENTE}] FALHA: a aplicação não respondeu a tempo. Logs recentes:"
$COMPOSE ps
$COMPOSE logs --tail=50
exit 1
