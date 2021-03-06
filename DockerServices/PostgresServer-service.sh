#!/usr/bin/env bash

option="${1}"
case ${option} in
    start)
        echo "Performing: start"
        docker pull postgres:11.2

        docker network inspect SpringCloudNetwork &> /dev/null || docker network create SpringCloudNetwork

        docker run -p 5432:5432     \
            --net SpringCloudNetwork    \
            --name PostgresDatabase   \
            -e POSTGRES_PASSWORD=rootroot   \
            -e POSTGRES_USER=dbo_admin   \
            -e POSTGRES_DB=cloud_db   \
            -d postgres:11.2
            #-v LOCAL_FOLDER:/var/libpostgresql/data  \

    ;;
    stop)
        echo "Performing: stop"
        docker stop PostgresDatabase
        docker rm PostgresDatabase

        docker network prune -f
    ;;
    log)
        echo "Performing: log"
        docker logs -f PostgresDatabase
    ;;
    *)
        echo "Not Valid Option for: ${0}, use [start], [stop] or [log]"
    ;;
esac

