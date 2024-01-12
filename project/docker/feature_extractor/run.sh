#!/bin/bash
FEATURE_EXTRACTOR_HOME="/home/app"


docker run -v "$PWD"/:"$FEATURE_EXTRACTOR_HOME"/volume  -e PROJECTS_DIR=./source_files -e CSV_OUTPUT_PATH=./metrics.csv -e ROLES_CSV_PATH=./roles.csv feature_extractor:latest