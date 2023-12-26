#!/bin/bash
CODE2VEC_HOME="/home/python"
MAX_ITERATIONS=20

docker run --rm -v "$PWD"/code2vec_models:"$CODE2VEC_HOME"/code2vec/volume -it -e CODE2VEC_HOME="$CODE2VEC_HOME" -e MAX_ITER="$MAX_ITERATIONS" code2vec:latest /bin/bash