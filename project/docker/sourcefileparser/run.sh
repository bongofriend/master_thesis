#!/bin/bash
SOURCE_FILE_PARSER_HOME="/home/app"

docker run -v "$PWD"/:"$SOURCE_FILE_PARSER_HOME"/volume  -e DATASET_PATH=./dataset -e OUTPUT_CSV=./metrics.csv sourcefileparser:latest