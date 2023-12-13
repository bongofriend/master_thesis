FROM quay.io/jupyter/base-notebook:lab-4.0.9
COPY ./docker/jupyter_server_config.py /home/jovyan/.jupyter/jupyter_server_config.py
COPY ./docker/jupyter_server_config.json /home/jovyan/.jupyter/jupyter_server_config.json
RUN pip install pandas scikit-learn plotly ipywidgets
RUN conda install --channel conda-forge --quiet --yes git
RUN pip install git+https://github.com/hyperopt/hyperopt-sklearn
ENTRYPOINT ["start-notebook.sh"]