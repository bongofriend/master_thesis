#!/bin/sh

docker exec -it $(docker ps -aqf 'name=project_jupyter_1')  /bin/bash