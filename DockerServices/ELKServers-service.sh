#!/usr/bin/env bash

C_DIR=D:\\Projects\\Github\\Spring-Cloud-Sample-Template\\DockerServices

option="${1}"
case ${option} in
    start)
        echo "Performing: start"

        docker pull elasticsearch:6.6.1
        docker pull kibana:6.6.1
        docker pull logstash:6.6.1

        docker network create ELK_network

        docker run -p 9200:9200             \
            -p 9300:9300                    \
            --net ELK_network               \
            --name elasticsearchserver      \
            -e "discovery.type=single-node" \
            -d elasticsearch:6.6.1
            #-v LOCAL_FOLDER:/usr/share/elasticsearch/data  \

        docker run -p 5601:5601             \
            --net ELK_network               \
            --name kibanaserver             \
            -v "$C_DIR\\conf\\kibana.yml:/usr/share/kibana/config/kibana.yml"  \
            -d kibana:6.6.1

         docker run -p 5000:5000            \
            --net ELK_network               \
            --name logstashserver           \
            -v "$C_DIR\\conf\\logstash.yml:/usr/share/logstash/config/logstash.yml"  \
            -v "$C_DIR\\conf\\logstash_pipelines\\:/usr/share/logstash/pipeline/"      \
            -d -it logstash:6.6.1

    ;;
    stop)
        echo "Performing: stop"

        docker stop logstashserver
        docker rm logstashserver

        docker stop kibanaserver
        docker rm kibanaserver

        docker stop elasticsearchserver
        docker rm elasticsearchserver

        docker network rm ELK_network
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
