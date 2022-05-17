FROM java:8
# Create app directory
WORKDIR /usr/src/nchu-tools-app
COPY target/nchu-tools-app.jar nchu-tools-app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /nchu-tools-app.jar ${0} ${@}"]
