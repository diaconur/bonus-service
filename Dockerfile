FROM openjdk:8u242

LABEL description="Docker image containing microservice with in H2 in memory database"

RUN mkdir myvolume | cd myvolume | touch newFile

VOLUME /myvolume

EXPOSE 8080

ADD ./target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]