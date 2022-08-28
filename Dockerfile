FROM amazoncorretto:11-alpine-jdk
COPY build/libs/ivanbot-log-processor.jar /ivanbot-log-processor.jar
ENTRYPOINT ["java","-jar","/ivanbot-log-processor.jar"]
