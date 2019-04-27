#!/bin/sh


docker rmi $(docker images -qa -f 'dangling=true')


docker network inspect SpringCloudNetwork &> /dev/null || docker network create SpringCloudNetwork


docker run -p 8888:8888     \
    --name ConfigServer     \
    --net SpringCloudNetwork    \
    -d herychemo/sample-cloud-config-server:0.0.1-SNAPSHOT

sleep 8

docker run -p 8761:8761     \
    --name EurekaServer     \
    --net SpringCloudNetwork    \
    -e SPRING_PROFILES_ACTIVE=dockercli \
    -d herychemo/sample-cloud-eureka-server:0.0.1-SNAPSHOT

sleep 10

docker run -p 9869:9869     \
    --name auth-ms     \
    --net SpringCloudNetwork    \
    -e SPRING_PROFILES_ACTIVE=dockercli \
    -d herychemo/sample-cloud-auth-ms:0.0.1-SNAPSHOT

sleep 6

docker run -p 8081:8081     \
    --name ZuulApiProxy     \
    --net SpringCloudNetwork    \
    -e SPRING_PROFILES_ACTIVE=dockercli \
    -d herychemo/sample-cloud-zuul-api-proxy:0.0.1-SNAPSHOT

sleep 6

docker run -p 9003:9003     \
    --name turbine-server     \
    --net SpringCloudNetwork    \
    -e SPRING_PROFILES_ACTIVE=dockercli \
    -d herychemo/sample-cloud-turbine-server:0.0.1-SNAPSHOT

sleep 6

docker run -p 9871:9871     \
    --name accounts-ms     \
    --net SpringCloudNetwork    \
    -e SPRING_PROFILES_ACTIVE=dockercli \
    -d herychemo/sample-cloud-accounts-ms:0.0.1-SNAPSHOT

sleep 6

docker run -p 9002:9002     \
    --name HystrixDashboard     \
    --net SpringCloudNetwork    \
    -e SPRING_PROFILES_ACTIVE=dockercli \
    -d herychemo/sample-cloud-hystrix-dashboard:0.0.1-SNAPSHOT

sleep 6

docker run -p 8760:8760     \
    --name SpringAdminServer     \
    --net SpringCloudNetwork    \
    -e SPRING_PROFILES_ACTIVE=dockercli \
    -d herychemo/sample-cloud-spring-admin-server:0.0.1-SNAPSHOT

sleep 4

docker run -p 8080:8080     \
    --name ui-gateway     \
    --net SpringCloudNetwork    \
    -e SPRING_PROFILES_ACTIVE=dockercli \
    -d herychemo/sample-cloud-ui-gateway:0.0.1-SNAPSHOT


sleep 1
