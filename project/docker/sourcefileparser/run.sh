#!/bin/bash
SOURCE_FILE_PARSER_HOME="/home/app"


docker run -v "$PWD"/:"$SOURCE_FILE_PARSER_HOME"/volume  -e PROJECTS_DIR=./source_files -e CSV_OUTPUT_PATH=./metrics.csv -e ROLES_CSV_PATH=./roles.csv sourcefileparser:latest