FROM openjdk:8-jre
MAINTAINER Heriberto Reyes Esparza <hery.chemo@gmail.com>

# Define Entry point
ENTRYPOINT ["/usr/bin/java", "-Djava.security.egd=file:/dev/./urandom", \
               "-jar", "/usr/share/application/Main.jar"]

# to use spring profiles add env var like: -e SPRING_PROFILES_ACTIVE=dev

# Add the service itself
ARG JAR_FILE
ADD target/${JAR_FILE} /usr/share/application/Main.jar

EXPOSE 9871
