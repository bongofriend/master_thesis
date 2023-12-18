FROM maven:3.9.6-eclipse-temurin-21 AS builder
ENV HOME /home/app
COPY ./sourcefileparser ${HOME}/sourcefileparser
WORKDIR ${HOME}/sourcefileparser
RUN mvn clean package

FROM eclipse-temurin:21-jre
ENV HOME /home/app
ENV DATASET_PATH ""
ENV OUTPUT_CSV ""
COPY  --from=builder ${HOME}/sourcefileparser/target/sourcefileparser-1.0-SNAPSHOT-jar-with-dependencies.jar ${HOME}/sourcefileparser.jar
WORKDIR ${HOME}
CMD  java -jar sourcefileparser.jar -s ./volume/${DATASET_PATH} -o ./volume/${OUTPUT_CSV}
