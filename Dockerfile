FROM openjdk:8-jdk-alpine
# Create app directory
WORKDIR /usr/src/nchu-tools-app
COPY . .
# Build Server
RUN mvn clean package -Dmaven.test.skip=true
COPY target/*.jar nchu-tools-app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /nchu-tools-app.jar ${0} ${@}"]
