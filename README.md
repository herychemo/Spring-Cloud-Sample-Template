# Spring-Cloud-Sample-Template
This is a Simple Spring Cloud Microservices Stack Sample Template.

**_Work in progress_**


## How to run:

__You may need to adapt some configurations according to your local filesystem.__

* In file: ELKServers-service.sh

    Change the var C_DIR, to your local DockerServices directory.
    
    Same for defining paths to xml/yml configurations.


### 1.- Run Docker Services 

Inside Docker Services Folder, run:

`sh start-all-services.sh`

It will start an instance of the following services using default ports:

* _RabbitMQ_
* _Elasticsearch_
* _Logstash_
* _Kibana_
* _Zipkin Server_
* _PostgreSQL_ 



### 2.- Run Spring Boot Projects.

Now you can start running the rest of the projects.

As some services have dependencies.

eg. All services need Configuration Server Running, or Eureka Server.

The recommended order to start all is:


1. ConfigServer
1. EurekaServer
1. AuthMs
1. ZuulProxy 
1. HystrixDashboard 
1. SpringAdminServer
1. AccountsMs


The configuration for all the projects is in a Configuration repository in:

https://github.com/herychemo/SampleConfigRepo 

Notice that the configured configuration is for Dev profile, so that you will need to run all projects using:

`-Dspring.profiles.active=dev`

eg. 
Inside ConfigServer folder

`mvn spring-boot:run -Dspring.profiles.active=dev`
