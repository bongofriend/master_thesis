FROM quay.io/jupyter/base-notebook:latest
COPY ./docker/jupyter_server_config.py /home/jovyan/.jupyter/jupyter_server_config.py
COPY ./docker/jupyter_server_config.json /home/jovyan/.jupyter/jupyter_server_config.json
RUN mamba install --quiet --yes pandas scikit-learn plotly
ENTRYPOINT ["start-notebook.sh"]