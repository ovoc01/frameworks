project = $1
server=/Users/rakotoharisoa/Documents/apache-tomcat-10.0.20/

cd frameworkTest/
jar -cf $project.war *
mv $project.war $server/webapps


javac -parameters -d /home/tendry/Framework/Framework /home/tendry/Framework/Framework/*.java
cd /home/tendry/Framework/Framework
jar -cf framework.jar etu2070
mv ./framework.jar /home/tendry/Framework/test-framework/WEB-INF/lib
cd /home/tendry/Framework/test-framework/WEB-INF/classes
javac -parameters -cp ../lib/framework.jar -d . *.java
cd /home/tendry/Framework/test-framework
jar -cf test-framework.war .
mv test-framework.war /home/tendry/apache-tomcat-10.1.7/webapps/
/home/tendry/apache-tomcat-10.1.7/bin/shutdown.sh
/home/tendry/apache-tomcat-10.1.7/bin/startup.sh