FROM maven:3.9.6-eclipse-temurin-21 AS builder
ENV FEATURE_EXTRACTOR_HOME /home/app
COPY ./feature_extractor ${FEATURE_EXTRACTOR_HOME}/feature_extractor
WORKDIR ${FEATURE_EXTRACTOR_HOME}/feature_extractor
RUN mvn dependency:go-offline -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn
RUN mvn clean package -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn


FROM eclipse-temurin:21-jre

ENV FEATURE_EXTRACTOR_HOME /home/app
ENV PROJECTS_DIR ""
ENV CSV_OUTPUT_PATH ""
ENV ROLES_CSV_PATH ""

COPY  --from=builder ${FEATURE_EXTRACTOR_HOME}/feature_extractor/target/feature_extractor-1.0-SNAPSHOT-jar-with-dependencies.jar ${FEATURE_EXTRACTOR_HOME}/feature_extractor.jar
WORKDIR ${FEATURE_EXTRACTOR_HOME}
ENTRYPOINT java -jar feature_extractor.jar -p ./volume/${PROJECTS_DIR} -o ./volume/${CSV_OUTPUT_PATH}  -r ./volume/${ROLES_CSV_PATH}