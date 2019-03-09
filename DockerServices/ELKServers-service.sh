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
            -e "discovery.type=single-node" \
            --name ElasticSearchServer      \
            -d elasticsearch:6.6.1
            #-v LOCAL_FOLDER:/usr/share/elasticsearch/data  \

        docker run -p 5601:5601             \
            --net ELK_network               \
            --name KibanaServer             \
            -v "$C_DIR\\conf\\kibana.yml:/usr/share/kibana/config/kibana.yml"  \
            -d kibana:6.6.1

         docker run -p 5000:5000            \
            --net ELK_network               \
            --name LogstashServer           \
            -v "$C_DIR\\conf\\logstash_pipelines\\:/usr/share/logstash/pipeline/"      \
            -d -it logstash:6.6.1
            #-v LOGSTASH_YML:/usr/share/logstash/config/logstash.yml  \

    ;;
    stop)
        echo "Performing: stop"

        docker stop LogstashServer
        docker rm LogstashServer

        docker stop KibanaServer
        docker rm KibanaServer

        docker stop ElasticSearchServer
        docker rm ElasticSearchServer

        docker network rm ELK_network
    ;;
    log)

        subOption="${2}"
        case ${subOption} in
            E)
                echo "Performing: log"
                docker logs -f ElasticSearchServer
            ;;
            L)
                echo "Performing: log"
                docker logs -f LogstashServer
            ;;
            K)
                echo "Performing: log"
                docker logs -f KibanaServer
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

