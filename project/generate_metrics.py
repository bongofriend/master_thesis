from os import path
import os
import argparse
import logging
import re
import sys
import class_evaluators
from models import RoleDescriptor, RoleEntry
import pandas as pd
from typing import List, Dict
from pathlib import Path
import javalang
from javalang.parser import JavaSyntaxError
from javalang.tree import Node
from collections import namedtuple
import time

#TODO  Add parallel processing to yielded files
#TODO Refactor source file resolution
#TODO Introduce better caching for parsed source files 


EnityNode = namedtuple('ClassNode', ['node', 'dp', 'micro_arch', 'role_entry'])
GeneratorResult = namedtuple('GeneratorResult', ['entity_node', 'exception'])

class Context:

    dataset_dir: str
    csv_file_path: str
    __metric_buffer: List[Dict[str, str]]
    __evaluaters: List[class_evaluators.MetricEvaluationInterface] = []
    __node_pool: Dict[str, Node] = {}

    def __init__(self, dataset_dir: str, csv_file_path: str) -> None:
        
        current_location = path.dirname(path.realpath(sys.argv[0]))
        self.dataset_dir = path.realpath(
            path.join(current_location, dataset_dir))
        self.csv_file_path = path.realpath(
            path.join(current_location, csv_file_path))
        self.__metric_buffer = []
        self.__evaluaters = [
            class_evaluators.NumberOfFieldsEvaluation(),
            class_evaluators.NumberOfStaticFieldsEvaluation(),
            class_evaluators.NumberOfMethodsEvaluation(),
            class_evaluators.NumberOfStaticMethodsEvaluation(),
            class_evaluators.NumberOfInterfacesEvaluation(),
            class_evaluators.NumberOfAbstractMethodsEvaluation(),
            class_evaluators.NumberOfOverriddenMethodsEvaluation(self),
            class_evaluators.NumberOfPrivateConstrcutorsEvaluation(),
            class_evaluators.NumberOfPrivateConstrcutorsEvaluation(),
            class_evaluators.NumberOfConstructorsWithObjectTypeArgumentEvaluation(),
            class_evaluators.NumberOfObjectFieldsEvaluation(),
            class_evaluators.NumberOfMethodGeneratingInstancesEvaluation(),
            class_evaluators.NumberOfOtherClassesWithFieldOfOwnTypeEvaluation(self)
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

    def resolve_dataset_dir(self, *p: str) -> str:
        return path.join(self.dataset_dir, *p)

    def get_evaluaters(self) -> List[class_evaluators.MetricEvaluationInterface]:
        return self.__evaluaters

    @classmethod
    def default(cls) -> "Context":
        return cls('./dataset', './metrics.csv')
    
    def parse_java_source_file(self, src_path: str):
        if src_path in self.__node_pool:
            return self.__node_pool[src_path]
        src_content= Path(src_path).read_bytes().decode('urf-8', 'ignore')
        if not re.search(r'enum\s[A-Z]{1,}', src_content):
            src_content = src_content.replace('enum', 'enumz')
        source_tree = javalang.parse.parse(src_content)
        self.__node_pool[src_path] = source_tree
        return source_tree


def parse_arguments() -> Context:
    parser = argparse.ArgumentParser(
        prog='Generate software metrics'
    )
    parser.add_argument('--datasetDir', '-d', type=str,
                        help='Path to directory of source files', dest='dataset_dir', required=True)
    parser.add_argument('--outputFile', '-o', type=str, dest='csv_file_path',
                        help='Output path for generated CSV file with calculated software metrics', required=True)
    args = parser.parse_args()
    return Context(args.dataset_dir, args.csv_file_path)


def get_source_file_path(ctx: Context, dp_path: str, micro_arch_path: str, entity_name: str) -> str | None:
    file_candidates = list(filter(lambda s: len(
        s) > 0 and s[0].isupper(), entity_name.split('.')))
    for c in file_candidates:
        p = f'{ctx.resolve_dataset_dir(dp_path, micro_arch_path, c)}.java'
        if path.exists(p) and path.isfile(p):
            return p
    return None

def get_entity_name(r: RoleEntry) -> str:
    classes =  filter(lambda s: s[0].isupper(), r.entity.split('.'))
    return (list(classes))[-1]


def parse_source_files(ctx: Context):
        for dp_dir in filter(lambda s: path.isdir(ctx.resolve_dataset_dir(s)), os.listdir(ctx.dataset_dir)):
            logging.info(f'Parsing design pattern {dp_dir}...')
            for micro_arch_dir in os.listdir(path.join(ctx.dataset_dir, dp_dir)):
                logging.info(f'Parsing micro architecture {micro_arch_dir}...')
                role_desc = RoleDescriptor.from_csv(
                    ctx.resolve_dataset_dir(dp_dir, micro_arch_dir))
                for role_entry in role_desc.roleEntries:
                    source_file_path = get_source_file_path(
                        ctx, dp_dir, micro_arch_dir, role_entry.entity)
                    if source_file_path is None:
                        continue
                    try:    
                        src_content: str = Path(
                                source_file_path).read_bytes().decode('utf-8', 'ignore')
                        if not re.search(r'enum\s[A-Z]{1,}', src_content):
                            src_content = src_content.replace('enum', 'enumz')
                        enitiy_name = get_entity_name(role_entry)
                        source_tree = javalang.parse.parse(src_content)
                        #source_tree = ctx.parse_java_source_file(source_file_path)
                        for _, node in source_tree:
                            if  (isinstance(node, javalang.tree.ClassDeclaration) or isinstance(node, javalang.tree.InterfaceDeclaration)) and hasattr(node, 'name') and node.name == enitiy_name:
                                yield GeneratorResult(EnityNode(node, dp_dir, micro_arch_dir, role_entry), None)
                    except JavaSyntaxError as j:
                        logging.error(j.description)
                        yield GeneratorResult(None, j)
                    except Exception as e:
                        logging.error(e.reason)
                        yield GeneratorResult(None, e)

def generate_metrics(ctx: Context):
        for n in parse_source_files(ctx):
            if n.exception is not None:
                continue
            evaluation_results: Dict[str, float] = {}
            for e in ctx.get_evaluaters():
                try:
                    evaluation_results[e.get_metric_name()] = e.evaluate(n.entity_node.node, n.entity_node.dp, n.entity_node.micro_arch)
                except JavaSyntaxError as j:
                    logging.error(j.description)
                    evaluation_results[e.get_metric_name()] = 0
                    continue   
                except Exception as ex:
                    logging.error(ex.reason)
                    evaluation_results[e.get_metric_name()] = 0
                    continue   
            ctx.add_metric_row(n.entity_node.dp, n.entity_node.micro_arch, n.entity_node.role_entry, evaluation_results)
         


if __name__ == '__main__':
    logging.basicConfig(level='INFO')
    #context = parse_arguments()
    context = Context.default()
    start = time.process_time()
    try:
        generate_metrics(context)
        context.save_metrics()
    except Exception as e:
        logging.error(e)
    finally:
        logging.info(f'Duration: {time.process_time() - start} s')
