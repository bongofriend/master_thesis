from dataclasses import dataclass, field
import subprocess
import io
import re
import psutil
import signal
import argparse
import pathlib
import os
import random
import shutil
from typing import List
import logging
from gensim.models import KeyedVectors as word2vec

@dataclass
class Args():
    max_iterations: int
    split_dataset: bool
    preprocess_dataset: bool
    export_model_iteration: int

@dataclass 
class Dataset():
    path: pathlib.Path
    ratio: float
    data: List[pathlib.Path] = field(default_factory=list)

def parse_args() -> Args:
    parser = argparse.ArgumentParser()
    parser.add_argument("--train_model", type=int, dest="max_iterations", default=0)
    parser.add_argument("--split_dataset", dest="split", action="store_true")
    parser.add_argument("--preprocess_dataset", dest="preprocess", action="store_true")
    parser.add_argument("--export_model", dest="export", type=int, default=0)
    args = parser.parse_args()
    return Args(args.max_iterations, args.split, args.preprocess, args.export)

def create_datasets():
    dataset_dir = pathlib.Path('./dataset')
    datasets = [Dataset(pathlib.Path(dataset_dir, 'train'), 0.75), Dataset(pathlib.Path(dataset_dir, 'test'), 0.15), Dataset(pathlib.Path(dataset_dir, 'validate'), 0.1)]

    logging.info('Splitting dataset...')
    design_pattern_paths = []
    for _, dirs, _ in os.walk(dataset_dir):
        for d in dirs:
            for dp_root, dp_dirs, _ in os.walk(pathlib.Path(dataset_dir, d)):
                design_pattern_paths.extend([pathlib.Path(dp_root, dp_dir) for dp_dir in dp_dirs])
    random.shuffle(design_pattern_paths)
    first_el = 0
    for dataset in datasets:
        last_el = int(len(design_pattern_paths) * dataset.ratio) + 1
        dataset.data.extend(design_pattern_paths[first_el:first_el+last_el].copy())
        first_el += last_el
    
    for d in datasets:
        for dp in d.data:
            dest_path = d.path.joinpath(dp.name)
            logging.info(f'Copying {dp.name} to {dest_path}...')
            shutil.move(dp, dest_path)

def preprocess():
    proc = subprocess.Popen(["/bin/bash", "./preprocess.sh"], stdout=subprocess.PIPE)
    for l in io.TextIOWrapper(proc.stdout, encoding="UTF-8"):
        print(f"{l.rstrip()}")

def train(args: Args):
    model_iter_regex = re.compile(r'saved_model_iter\d{1,}$')
    proc = subprocess.Popen(["/bin/bash", "train.sh"], stdout=subprocess.PIPE)
    for l in io.TextIOWrapper(proc.stdout, encoding="UTF-8"):
        print(f"{l.rstrip()}")
        if not model_iter_regex.search(l):
            continue
        iteration = int((model_iter_regex.findall(l)[0]).replace("saved_model_iter", ""))
        if iteration == args.max_iterations:
            for p in psutil.Process(proc.pid).children(recursive=True):
                p.send_signal(signal.SIGINT)
            proc.send_signal(signal.SIGINT)
            return

def export_model(args: Args):
    model_path = pathlib.Path(f"volume/models/my_dataset/saved_model_iter{args.export_model_iteration}").absolute()
    subprocess.call(['python3', 'code2vec.py', '--load', model_path, '--release'])
    release_mode_path = pathlib.Path(f"{model_path}.release").absolute()
    token_path = pathlib.Path("volume/models/token.txt")
    subprocess.call(["python3", "code2vec.py", "--load", release_mode_path, "--save_w2v", token_path])
    #subprocess.call(["python3", "code2vec.py", "--load", release_mode_path, "--save_t2v", pathlib.Path("volume/models/target.txt")])
    saved_model_path = pathlib.Path('volume/models/model.bin')
    model = word2vec.load_word2vec_format(token_path, binary=False)
    model.save_word2vec_format(saved_model_path, binary=True)

if __name__ == '__main__':
    logging.basicConfig(level='INFO')
    args = parse_args()
    if args.split_dataset:
        create_datasets()
    if args.preprocess_dataset:
        preprocess()
    if args.max_iterations > 0:
        train(args)
    if args.export_model_iteration > 0:    
        export_model(args)
        