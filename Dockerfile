FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/*.jar excel-process-app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /excel-process-app.jar ${0} ${@}"]
