#!/usr/bin/env bash

option="${1}"
case ${option} in
    start)
        echo "Performing: start"
        docker pull openzipkin/zipkin

        docker network inspect SpringCloudNetwork &> /dev/null || docker network create SpringCloudNetwork

        docker run -p 9411:9411     \
            --net SpringCloudNetwork    \
            --name ZipkinServer     \
            -d openzipkin/zipkin

    ;;
    stop)
        echo "Performing: stop"
        docker stop ZipkinServer
        docker rm ZipkinServer

        docker network prune -f
    ;;
    log)
        echo "Performing: log"
        docker logs -f ZipkinServer
    ;;
    *)
        echo "Not Valid Option for: ${0}, use [start], [stop] or [log]"
    ;;
esac

