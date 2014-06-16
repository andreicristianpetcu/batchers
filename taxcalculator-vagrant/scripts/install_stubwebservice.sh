#!/usr/bin/env bash
set -e

export DISPLAY=:10

cd $HOME
# unarchive tomcat
tar -xf apache-tomcat-7.0.54.tar.gz

mkdir  apache-tomcat-7.0.54-stubwebservice
tar --strip-components 1 -C apache-tomcat-7.0.54-stubwebservice -xzf apache-tomcat-7.0.54.tar.gz

#webservice REST port
sed -i 's/"8080"/"9091"/g' $HOME/apache-tomcat-7.0.54-stubwebservice/conf/server.xml

#override tomcat management ports so they don't conflict with presentation:
sed -i 's/"8009"/"8010"/g' $HOME/apache-tomcat-7.0.54-stubwebservice/conf/server.xml
sed -i 's/"8005"/"8006"/g' $HOME/apache-tomcat-7.0.54-stubwebservice/conf/server.xml

rm -rf $HOME/apache-tomcat-7.0.54-stubwebservice/webapps/taxcalculator-*
cp $HOME/batchers/taxcalculator/taxcalculator-stubwebservice/target/taxcalculator-stubwebservice-1.0-SNAPSHOT.war $HOME/apache-tomcat-7.0.54-stubwebservice/webapps/stubwebservice.war

apache-tomcat-7.0.54-stubwebservice/bin/catalina.sh start

