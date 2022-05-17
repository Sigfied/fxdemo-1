docker run --name nchu-tools-app \
    --restart always \
    -d \
    -p 3333:3333 \
    -e "JAVA_OPTS=-Ddebug -Xmx128m" \
    ayayating1031/nchu-tools-app --server.port=3333