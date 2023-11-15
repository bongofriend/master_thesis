import abc
from os import path
import os
import argparse
import logging
import sys
from models import RoleDescriptor, RoleEntry
import pandas as pd
from typing import List, Dict
from pathlib import Path


class MeticEvaluaterInterface(metaclass=abc.ABCMeta):

    @classmethod
    def __subclasshook__(cls, subclass):
        return (
            hasattr(subclass, 'get_metric_name') and callable(subclass.get_metric_name) and
            hasattr(subclass, 'evaluate') and callable(subclass.evaluate) or
            NotImplemented
        )

    @abc.abstractmethod
    def get_metric_name(self) -> str:
        raise NotImplementedError

    @abc.abstractmethod
    def evaluate(self, source_file_content: str) -> float:
        raise NotImplementedError



class Context:

    source_files_dir: str
    csv_file_path: str
    __metric_buffer: List[Dict[str, str]]
    __evaluaters: List[MeticEvaluaterInterface] = []

    def __init__(self, source_files_dir: str, csv_file_path: str) -> None:
        current_location = path.dirname(path.realpath(sys.argv[0]))
        self.source_files_dir = path.realpath(
            path.join(current_location, source_files_dir))
        self.csv_file_path = path.realpath(
            path.join(current_location, csv_file_path))
        self.__metric_buffer = []
        #TODO: Determine evaluaters and implement them
        self.__evaluaters = [
           
        ]

    def add_metric_row(self, dp: str, micro_arch: str, role: RoleEntry, metrics: Dict[str, float]):
        metric_entry: Dict[str, str] = {}
        metric_entry['role'] = role.role
        metric_entry['role_kind'] = role.role_kind
        metric_entry['entity'] = role.entity
        metric_entry['design_pattern'] = dp
        metric_entry['micro_architecture'] = micro_arch
        metric_entry = metric_entry | metrics

        self.__metric_buffer.append(metric_entry)

    def save_metrics(self):
        pd.DataFrame(self.__metric_buffer).to_csv(
            path_or_buf=self.csv_file_path, index=False)

    def resolve_source_dir_path(self, *p: str) -> str:
        return path.join(self.source_files_dir, *p)

    def get_evaluaters(self) -> List[MeticEvaluaterInterface]:
        return self.__evaluaters


def parse_arguments() -> Context:
    parser = argparse.ArgumentParser(
        prog='Generate software metrics'
    )
    parser.add_argument('--sourceFilesDir', '-s', type=str,
                        help='Path to directory of source files', dest='source_files_dir', required=True)
    parser.add_argument('--outputFile', '-o', type=str, dest='csv_file_path',
                        help='Output path for generated CSV file with calculated software metrics', required=True)
    args = parser.parse_args()
    return Context(args.source_files_dir, args.csv_file_path)


def get_source_file_path(ctx: Context, dp_path: str, micro_arch_path: str, entity_name: str) -> str | None:
    file_candidates = list(filter(lambda s: len(
        s) > 0 and s[0].isupper(), entity_name.split('.')))
    for c in file_candidates:
        p = f'{ctx.resolve_source_dir_path(dp_path, micro_arch_path, c)}.java'
        if path.exists(p) and path.isfile(p):
            return p
    return None


def parse_source_files(ctx: Context, dp_dir: str, micro_arch_dir: str):
    role_desc = RoleDescriptor.from_csv(
        ctx.resolve_source_dir_path(dp_dir, micro_arch_dir))
    for role_entry in role_desc.roleEntries:
        source_file_path = get_source_file_path(
            ctx, dp_dir, micro_arch_dir, role_entry.entity)
        if source_file_path is None:
            continue
        source_file_content: str = Path(
            source_file_path).read_text(encoding='latin-1')
        evaluation_results: Dict[str, float] = {}
        for evaluator in ctx.get_evaluaters():
            evaluation_results[evaluator.get_metric_name(
            )] = evaluator.evaluate(source_file_content)
        ctx.add_metric_row(dp_dir, micro_arch_dir, role_entry, evaluation_results)


def generate_metrics(ctx: Context):
    for dp_dir in filter(lambda s: path.isdir(ctx.resolve_source_dir_path(s)), os.listdir(ctx.source_files_dir)):
        logging.info(f'Parsing design pattern {dp_dir}...')
        for micro_arch_dir in os.listdir(path.join(ctx.source_files_dir, dp_dir)):
            logging.info(f'Parsing micro architecture {micro_arch_dir}...')
            parse_source_files(ctx, dp_dir, micro_arch_dir)


if __name__ == '__main__':
    logging.basicConfig(level='INFO')
    context = parse_arguments()
    try:
        generate_metrics(context)
        context.save_metrics()
    except Exception as e:
        logging.error(e)
