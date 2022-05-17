export BASE_PATH=$(cd `dirname $0`;pwd)
cd $BASE_PATH
mvn clean package
docker build -t nchu-tools-app .
