FROM maven:3.9.6-eclipse-temurin-21 AS builder
ENV SOURCE_FILE_PARSER_HOME /home/app
COPY ./sourcefileparser ${SOURCE_FILE_PARSER_HOME}/sourcefileparser
WORKDIR ${SOURCE_FILE_PARSER_HOME}/sourcefileparser
RUN mvn clean package

FROM eclipse-temurin:21-jre
ENV SOURCE_FILE_PARSER_HOME /home/app
ENV DATASET_PATH ""
ENV OUTPUT_CSV ""
ENV INCLUDE_CK_METRICS "true"
COPY  --from=builder ${SOURCE_FILE_PARSER_HOME}/sourcefileparser/target/sourcefileparser-1.0-SNAPSHOT-jar-with-dependencies.jar ${SOURCE_FILE_PARSER_HOME}/sourcefileparser.jar
WORKDIR ${SOURCE_FILE_PARSER_HOME}
ENTRYPOINT java -jar sourcefileparser.jar -s ./volume/${DATASET_PATH} -o ./volume/${OUTPUT_CSV} -ck ${INCLUDE_CK_METRICS}