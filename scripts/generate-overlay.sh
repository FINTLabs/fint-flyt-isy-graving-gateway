#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
OVERLAYS_ROOT="${REPO_ROOT}/kustomize/overlays"
TEMPLATE="${REPO_ROOT}/scripts/templates/overlay-template.yaml"

if [ ! -f "$TEMPLATE" ]; then
  echo "Template not found at $TEMPLATE" >&2
  exit 1
fi

render() {
  local template_path="$1"
  local output_path="$2"
  if command -v envsubst >/dev/null 2>&1; then
    envsubst < "$template_path" > "$output_path"
  else
    python3 - "$template_path" "$output_path" <<'PY'
import os, sys
template_path, output_path = sys.argv[1], sys.argv[2]
with open(template_path, "r", encoding="utf-8") as f:
    content = f.read()
rendered = os.path.expandvars(content)
with open(output_path, "w", encoding="utf-8") as f:
    f.write(rendered)
PY
  fi
}

FOUND=0
for ENV_DIR in "$OVERLAYS_ROOT"/*/*; do
  [ -d "$ENV_DIR" ] || continue
  ENVIRONMENT=$(basename "$ENV_DIR")
  case "$ENVIRONMENT" in
    api|beta) ;;
    *) continue ;;
  esac

  ORG_SLUG=$(basename "$(dirname "$ENV_DIR")")
  ORG_DOT=${ORG_SLUG//-/.}
  ORG_UNDERSCORE=${ORG_SLUG//-/_}

  if [ "$ENVIRONMENT" = "beta" ]; then
    SERVLET_CONTEXT_PATH="/beta/${ORG_SLUG}"
    INGRESS_BASE_PATH="/beta/${ORG_SLUG}/api/isygraving/instances"
  else
    SERVLET_CONTEXT_PATH="/${ORG_SLUG}"
    INGRESS_BASE_PATH="/${ORG_SLUG}/api/isygraving/instances"
  fi

  export NAMESPACE="$ORG_SLUG"
  export APP_INSTANCE="flyt-isy-graving-gateway_${ORG_UNDERSCORE}"
  export ORG_DOT
  export ORG_SLUG
  export SERVLET_CONTEXT_PATH
  export INGRESS_BASE_PATH

  OUTPUT="${ENV_DIR}/kustomization.yaml"
  mkdir -p "$ENV_DIR"
  render "$TEMPLATE" "$OUTPUT"
  echo "Generated ${OUTPUT}"
  FOUND=1
done

if [ "$FOUND" -eq 0 ]; then
  echo "No overlays found under ${OVERLAYS_ROOT}/*/(api|beta)" >&2
  exit 1
fi
