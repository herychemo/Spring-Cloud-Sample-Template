# Spring-Cloud-Sample-Template
This is a Simple Spring Cloud MicroServices Stack Sample Template.

**_Work in progress_**

## 1.- How to test:

This project requires some services running to work properly, 
like a PostgreSQL database, ELK stack, RabbitMQ server, etc. 
You can use your own servers or use docker containers.


### 1.1 Run as Spring Boot applications.

You can run the projects as Spring boot applications, 
for this first make sure to have the dependent services up and running.
The required services are:  

* _RabbitMQ_
* _Elasticsearch_
* _Logstash_
* _Kibana_
* _Zipkin Server_
* _PostgreSQL_ 

You can use sample scripts in DockerServices folder, in order to configure these services.
A sample configuration also can be found in Docker-compose.yml file.

After setting up those services, just build and run the apps in your local workspace. 

As some services might depend on other ones, the recommended startup order for Spring boot Apps is:

1. ConfigServer
1. EurekaServer
1. AuthMs
1. ZuulApiProxy
1. turbine-server
1. AccountsMs
1. HystrixDashboard
1. SpringAdminServer
1. ui-gateway


The configuration for all the projects is in the Configuration repository [SampleConfigRepo](https://gitlab.com/herychemo/SampleConfigRepo).

The configuration for a local setup uses profile local. It will load all the configuration files from configuration repo, pointing to localhost.
Activate the environment using _spring.profiles.active_ like: 

```bash
java -jar -Dspring.profiles.active=local SomeProject.jar
```

eg. 
Inside EurekaServer folder

`mvn spring-boot:run -Dspring.profiles.active=local`


### 1.2 Run in a docker swarm

For running services in docker swarm mode, just do:

```bash 
mvn clean package
docker stack deploy -c docker-compose.yml cloud
```

All the required services mentioned earlier are configured in the docker-compose.yml file. 

