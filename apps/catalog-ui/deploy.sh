#!/usr/bin/env bash
set -euo pipefail

# ─── Config ────────────────────────────────────────────────
SSH_HOST="${DEPLOY_HOST:-flowforgelive}"
IMAGE_NAME="uikit-catalog"
COMPOSE_DIR="/opt/flowforgelive"
DOCKERFILE="apps/catalog-ui/react/Dockerfile"
# ───────────────────────────────────────────────────────────

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

cd "$PROJECT_ROOT"

echo "==> Building KMP JS libraries..."
./gradlew :core:uikit:common:jsBrowserProductionLibraryDistribution \
          :apps:catalog-ui:shared:jsBrowserProductionLibraryDistribution \
          --console=plain

echo "==> Installing npm dependencies..."
(cd core/uikit/react && npm install)
(cd apps/catalog-ui/react && npm install)

echo "==> Building Next.js standalone..."
(cd apps/catalog-ui/react && npm run build)

echo "==> Building Docker image locally..."
docker build --platform linux/amd64 -f "$DOCKERFILE" -t "$IMAGE_NAME:latest" .

echo "==> Saving image to archive..."
docker save "$IMAGE_NAME:latest" | gzip > /tmp/${IMAGE_NAME}.tar.gz

echo "==> Uploading image to ${SSH_HOST}..."
scp /tmp/${IMAGE_NAME}.tar.gz "${SSH_HOST}:/tmp/${IMAGE_NAME}.tar.gz"

echo "==> Loading image & restarting on server..."
ssh "$SSH_HOST" "
  docker load < /tmp/${IMAGE_NAME}.tar.gz
  cd ${COMPOSE_DIR}
  docker compose up -d --force-recreate ${IMAGE_NAME}
  docker image prune -f
  rm -f /tmp/${IMAGE_NAME}.tar.gz
"

rm -f /tmp/${IMAGE_NAME}.tar.gz
echo "==> Deploy complete!"
