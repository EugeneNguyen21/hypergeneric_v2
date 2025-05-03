@echo off
echo Starting Hypergeneric application...
set MAVEN_OPTS=-Dspring.output.ansi.enabled=ALWAYS
mvn -s settings.xml clean spring-boot:run
