export BASE_PATH=$(cd `dirname $0`;pwd)
cd $BASE_PATH
mvn clean pckage
docker build .
docker run -p 3333:3333 -e "JAVA_OPTS=-Ddebug -Xmx128m" excel-process-app --server.port=3333