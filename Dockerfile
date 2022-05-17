FROM openjdk:8-jdk-alpine
# Create app directory
WORKDIR /usr/src/nchu-tools-app
COPY target/*.jar nchu-tools-app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /usr/src/nchu-tools-app/nchu-tools-app.jar ${0} ${@}"]
