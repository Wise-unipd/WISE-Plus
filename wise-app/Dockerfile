# STAGE 1: build -> base image with Maven and JDK
FROM maven:3.9.14-eclipse-temurin-21-noble AS build

# set the working directory in the container
WORKDIR /app

# copy the configuration file
COPY pom.xml .
# download the required dependencies and plugins (only when the pom.xml file changes, thanks to Docker layer cache)
RUN mvn dependency:go-offline -B

# copy the source code
COPY src ./src
# compile the code and create the .war file
RUN mvn clean package

# STAGE 2: run -> Tomcat image (JRE only, because we don't need a compiler)
FROM tomcat:11.0.20-jre21-temurin-noble

# set the working directory in the container
WORKDIR /usr/local/tomcat

# clean the Tomcat applications folder
RUN rm -rf webapps/*

# move the .war file created in STAGE 1 here
COPY --from=build /app/target/*.war webapps/ROOT.war

# documentation: the container listens on port 8080
EXPOSE 8080

# start Tomcat in the foreground
CMD ["catalina.sh", "run"]
