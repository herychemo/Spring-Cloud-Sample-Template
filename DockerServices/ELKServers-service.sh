#!/usr/bin/env bash

option="${1}"
case ${option} in
    start)
        echo "Performing: start"

        docker pull elasticsearch:6.6.1
        docker pull kibana:6.6.1
        docker pull logstash:6.6.1

        docker build -t grayraccoon/sample-cloud-elasticsearch:latest -f E_Dockerfile .
        docker build -t grayraccoon/sample-cloud-kibana:latest -f K_Dockerfile .
        docker build -t grayraccoon/sample-cloud-logstash:latest -f L_Dockerfile .

        docker network inspect SpringCloudNetwork &> /dev/null || docker network create SpringCloudNetwork

        docker run -p 9200:9200             \
            -p 9300:9300                    \
            --net SpringCloudNetwork        \
            --name elasticsearchserver      \
            -d grayraccoon/sample-cloud-elasticsearch:latest
            #-v LOCAL_FOLDER:/usr/share/elasticsearch/data  \

        docker run -p 5601:5601             \
            --net SpringCloudNetwork        \
            --name kibanaserver             \
            -d grayraccoon/sample-cloud-kibana:latest

         docker run -p 5000:5000            \
            --net SpringCloudNetwork        \
            --name logstashserver           \
            -d -it grayraccoon/sample-cloud-logstash:latest

    ;;
    stop)
        echo "Performing: stop"

        docker stop logstashserver
        docker rm logstashserver

        docker stop kibanaserver
        docker rm kibanaserver

        docker stop elasticsearchserver
        docker rm elasticsearchserver

        #docker network rm SpringCloudNetwork
        docker network prune -f
    ;;
    log)

        subOption="${2}"
        case ${subOption} in
            E)
                echo "Performing: log"
                docker logs -f elasticsearchserver
            ;;
            L)
                echo "Performing: log"
                docker logs -f logstashserver
            ;;
            K)
                echo "Performing: log"
                docker logs -f kibanaserver
            ;;
            *)
                echo "Not valid sub option for: ${0} ${1}, use [E], [L] or [K]"
            ;;
        esac
    ;;
    *)
        echo "Not Valid Option for: ${0}, use [start], [stop] or [log]"
    ;;
esac

