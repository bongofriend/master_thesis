FROM maven:3.9.6-eclipse-temurin-21 AS builder
ENV SOURCE_FILE_PARSER_HOME /home/app
COPY ./sourcefileparser ${SOURCE_FILE_PARSER_HOME}/sourcefileparser
WORKDIR ${SOURCE_FILE_PARSER_HOME}/sourcefileparser
RUN mvn dependency:go-offline -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn
RUN mvn clean package -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn


FROM eclipse-temurin:21-jre

ENV SOURCE_FILE_PARSER_HOME /home/app
ENV PROJECTS_DIR ""
ENV CSV_OUTPUT_PATH ""
ENV ROLES_CSV_PATH ""

COPY  --from=builder ${SOURCE_FILE_PARSER_HOME}/sourcefileparser/target/sourcefileparser-1.0-SNAPSHOT-jar-with-dependencies.jar ${SOURCE_FILE_PARSER_HOME}/sourcefileparser.jar
WORKDIR ${SOURCE_FILE_PARSER_HOME}
ENTRYPOINT java -jar sourcefileparser.jar -p ./volume/${PROJECTS_DIR} -o ./volume/${CSV_OUTPUT_PATH}  -r ./volume/${ROLES_CSV_PATH}