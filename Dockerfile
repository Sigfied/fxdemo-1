FROM openjdk:8-jdk-alpine
# Create app directory
WORKDIR /usr/src/nchu-tools-app
COPY . .
# Build Server
RUN mvn clean package
COPY target/*.jar excel-process-app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /excel-process-app.jar ${0} ${@}"]
