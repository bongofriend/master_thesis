from dataclasses import dataclass
import subprocess
import io
import re
import psutil
import signal
import argparse
import pathlib


@dataclass
class Args():
    max_iterations: int

def parse_args() -> Args:
    parser = argparse.ArgumentParser()
    parser.add_argument("--maxIterations", type=int, dest="max_iterations", default=1)
    args = parser.parse_args()
    return Args(args.max_iterations)

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
    model_path = pathlib.Path(f"volume/models/my_dataset/saved_model_iter{args.max_iterations}").absolute()
    subprocess.call(['python3', 'code2vec.py', '--load', model_path, '--release'])
    release_mode_path = pathlib.Path(f"{model_path}.release").absolute()
    #python3 code2vec.py --load models/java14_model/saved_model_iter8.release --save_w2v models/java14_model/tokens.txt
    subprocess.call(["python3", "code2vec.py", "--load", release_mode_path, "--save_w2v", pathlib.Path("volume/models/token.txt")])
    subprocess.call(["python3", "code2vec.py", "--load", release_mode_path, "--save_t2v", pathlib.Path("volume/models/target.txt")])

if __name__ == '__main__':
    args = parse_args()
    preprocess()
    train(args)
    export_model(args)
        