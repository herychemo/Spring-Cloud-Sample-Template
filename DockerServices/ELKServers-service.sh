#!/usr/bin/env bash

C_DIR=D:\\Projects\\Github\\Spring-Cloud-Sample-Template\\DockerServices

option="${1}"
case ${option} in
    start)
        echo "Performing: start"

        docker pull elasticsearch:7.0.0
        docker pull kibana:7.0.0
        docker pull logstash:7.0.0

        docker network inspect SpringCloudNetwork &> /dev/null || docker network create SpringCloudNetwork

        docker run -p 9200:9200             \
            -p 9300:9300                    \
            --net SpringCloudNetwork        \
            --name elasticsearchserver      \
            -e "discovery.type=single-node" \
            -d elasticsearch:7.0.0
            #-v LOCAL_FOLDER:/usr/share/elasticsearch/data  \

        docker run -p 5601:5601             \
            --net SpringCloudNetwork        \
            --name kibanaserver             \
            -e ELASTICSEARCH_URL=http://elasticsearchserver:9200    \
            -v "$C_DIR\\conf\\kibana\\config\\kibana.yml:/usr/share/kibana/config/kibana.yml"  \
            -d kibana:7.0.0

         docker run -p 5000:5000            \
            --net SpringCloudNetwork        \
            --name logstashserver           \
            -e RABBIT_MQ_HOST=RabbitMQ-Server       \
            -e ELASTICSEARCH_HOST=elasticsearchserver   \
            -v "$C_DIR\\conf\\logstash\\config\\logstash.yml:/usr/share/logstash/config/logstash.yml"  \
            -v "$C_DIR\\conf\\logstash\\pipeline\\:/usr/share/logstash/pipeline/"      \
            -d -it logstash:7.0.0

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

