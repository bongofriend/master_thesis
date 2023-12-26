FROM python:3.7.17-bullseye

ENV CODE2VEC_HOME /home/python
ENV MAX_ITER 1

#Java installation
USER root
RUN apt install -y wget apt-transport-https gnupg
RUN wget -O - https://packages.adoptium.net/artifactory/api/gpg/key/public | apt-key add -
RUN echo "deb https://packages.adoptium.net/artifactory/deb $(awk -F= '/^VERSION_CODENAME/{print$2}' /etc/os-release) main" | tee /etc/apt/sources.list.d/adoptium.list
RUN apt update
RUN apt install -y temurin-17-jdk

#Python installation
COPY ./code2vec ${CODE2VEC_HOME}/code2vec
WORKDIR ${CODE2VEC_HOME}/code2vec
RUN pip3 install -r requirements.txt
COPY ./dataset ${CODE2VEC_HOME}/code2vec/dataset
COPY ./docker/code2vec/helper.py ${CODE2VEC_HOME}/code2vec/helper.py
#ENTRYPOINT python3 helper.py --maxIterations ${MAX_ITER}