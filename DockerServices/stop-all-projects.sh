#!/bin/sh

docker stop ConfigServer
docker rm ConfigServer

docker stop EurekaServer
docker rm EurekaServer


docker network prune -f