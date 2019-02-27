docker pull openzipkin/zipkin

docker run -p 9411:9411 --name ZipkinServer -d openzipkin/zipkin  

docker logs -f ZipkinServer
