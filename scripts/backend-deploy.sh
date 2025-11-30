#!/bin/bash
set -e
cd /home/ubuntu/backend

export $(cat .env | xargs)

echo "$DOCKER_TOKEN" | docker login -u "$DOCKER_USERNAME" --password-stdin

docker compose pull backend
docker compose up -d backend

docker logout
docker image prune -f