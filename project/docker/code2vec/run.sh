#!/bin/bash
CODE2VEC_HOME="/home/python"
MAX_ITERATIONS=1

docker run -v --rm "$PWD"/code2vec_models:"$CODE2VEC_HOME"/code2vec/volume -e CODE2VEC_HOME="$CODE2VEC_HOME" -e MAX_ITER="$MAX_ITER" code2vec:latest