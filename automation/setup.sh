#!/bin/bash

echo "Configurando RabbitMQ"

curl -XPUT -s -o /dev/null -w "%{http_code}\n" -u guest:guest "http://localhost:15672/api/exchanges/%2F/KEEPALIVE_EVENTS" -d '{"type":"fanout","auto_delete":false,"durable":false,"internal":false}'
curl -XPUT -s -o /dev/null -w "%{http_code}\n" -u guest:guest "http://localhost:15672/api/queues/%2F/STREAM_EVENT_AGGREGATOR" -d '{"auto_delete":false,"durable":true}'
curl -XPUT -s -o /dev/null -w "%{http_code}\n" -u guest:guest "http://localhost:15672/api/queues/%2F/STREAM_REALTIME_DASHBOARD" -d '{"auto_delete":false,"durable":true}'
curl -XPOST -s -o /dev/null -w "%{http_code}\n" -u guest:guest "http://localhost:15672/api/bindings/%2F/e/KEEPALIVE_EVENTS/q/STREAM_EVENT_AGGREGATOR"
curl -XPOST -s -o /dev/null -w "%{http_code}\n" -u guest:guest "http://localhost:15672/api/bindings/%2F/e/KEEPALIVE_EVENTS/q/STREAM_REALTIME_DASHBOARD"

echo "Configurando PubSub do Redis"

echo "config set notify-keyspace-events Ex" | docker exec -i redis redis-cli -h redis

echo "Pode ir buscar um cafe..."
echo "Inserindo 100 mil registros de restaurantes no MongoDB - isso vai demorar alguns minutos"

docker exec -i mongodb mongo restaurante < database/fill_restaurants.js
