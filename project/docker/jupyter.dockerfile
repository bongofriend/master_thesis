FROM quay.io/jupyter/base-notebook:lab-4.0.9
COPY ./docker/jupyter_server_config.py /home/jovyan/.jupyter/jupyter_server_config.py
COPY ./docker/jupyter_server_config.json /home/jovyan/.jupyter/jupyter_server_config.json
RUN mamba install  -c conda-forge --quiet --yes pandas scikit-learn plotly ipywidgets
ENTRYPOINT ["start-notebook.sh"]