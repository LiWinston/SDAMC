# build stage
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .
RUN mvn clean install

# run stage
FROM tomcat:10.1.24-jdk21

COPY --from=build /app/target/SDAMC-1.0-SNAPSHOT.war $CATALINA_HOME/webapps/ROOT.war
