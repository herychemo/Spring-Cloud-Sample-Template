#!/bin/sh

docker rmi $(docker images -qa -f 'dangling=true')

docker network inspect SpringCloudNetwork &> /dev/null || docker network create SpringCloudNetwork


docker run -p 8888:8888     \
    --name ConfigServer     \
    --net SpringCloudNetwork    \
    -d herychemo/sample-cloud-config-server:0.0.1-SNAPSHOT

sleep 4

docker run -p 8761:8761     \
    --name EurekaServer     \
    --net SpringCloudNetwork    \
    -e SPRING_PROFILES_ACTIVE=dev \
    -e CONFIG_SERVER=http://ConfigServer:8888 \
    -d herychemo/sample-cloud-eureka-server:0.0.1-SNAPSHOT

sleep 1

