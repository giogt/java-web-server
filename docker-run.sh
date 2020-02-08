#!/usr/bin/env sh

SCRIPT=$(readlink -f "$0")
DIR=$(dirname "$SCRIPT")

docker run -i -t \
  --rm \
  --name="giogt-web-server" \
  -p 8080:8080 \
  -e "ORG_GIOGT_WEB_SERVER_ROOTDIRPATH=/web/root" \
  -v "${DIR}/root/main:/web/root" \
  giogt/web-server:latest
