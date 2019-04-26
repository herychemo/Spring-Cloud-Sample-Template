#!/bin/sh

docker stop ui-gateway
docker rm ui-gateway

docker stop SpringAdminServer
docker rm SpringAdminServer

docker stop HystrixDashboard
docker rm HystrixDashboard

docker stop accounts-ms
docker rm accounts-ms

docker stop TurbineServer
docker rm TurbineServer

docker stop ZuulApiProxy
docker rm ZuulApiProxy

docker stop auth-ms
docker rm auth-ms

docker stop EurekaServer
docker rm EurekaServer

docker stop ConfigServer
docker rm ConfigServer


docker network prune -f
