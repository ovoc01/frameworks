project = $1
server=/Users/rakotoharisoa/Documents/apache-tomcat-10.0.20/

cd frameworkTest/
jar -cf $project.war *
mv $project.war $server/webapps