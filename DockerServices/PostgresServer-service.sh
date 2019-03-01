#!/usr/bin/env bash

option="${1}"
case ${option} in
    start)
        echo "Performing: start"
        docker pull postgres:11.2

        docker run -p 5432:5432     \
            --name PostgresDatabaseSpringCloudSample   \
            -e POSTGRES_PASSWORD=rootroot   \
            -d postgres:11.2
            #-v LOCAL_FOLDER:/var/libpostgresql/data  \

    ;;
    stop)
        echo "Performing: stop"
        docker stop PostgresDatabaseSpringCloudSample
        docker rm PostgresDatabaseSpringCloudSample
    ;;
    log)
        echo "Performing: log"
        docker logs -f PostgresDatabaseSpringCloudSample
    ;;
    *)
        echo "Not Valid Option for: ${0}, use [start], [stop] or [log]"
    ;;
esac

