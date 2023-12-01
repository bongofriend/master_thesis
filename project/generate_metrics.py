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
from dataclasses import dataclass, field

#TODO Add parallel processing to yielded files


EnityNode = namedtuple('ClassNode', ['node', 'dp', 'micro_arch', 'role_entry'])
GeneratorResult = namedtuple('GeneratorResult', ['entity_node', 'exception'])


@dataclass
class GeneratorValue:
    node: Node
    dp: str
    micro_arch: str
    role_entry: RoleEntry

@dataclass
class GeneratorResult:    
    value: GeneratorValue | None = field(default=None)
    exception_msg: str | None = field(default=None)

    @classmethod
    def with_value(cls, value: GeneratorValue) -> GeneratorResult:
        return cls(value, None)
    
    @classmethod
    def with_error(cls, exception_msg: str) -> GeneratorResult:
        return cls(None, exception_msg)

class Resolver:
    dataset_dir: str
    csv_file_path: str

    def __init__(self, dataset_dir: str, csv_file_path: str) -> None:
        
        current_location = path.dirname(path.realpath(sys.argv[0]))
        self.dataset_dir = path.realpath(
            path.join(current_location, dataset_dir))
        self.csv_file_path = path.realpath(
            path.join(current_location, csv_file_path))
      
   
    def resolve_source_file_path(self, *path_segments: str, entity_name: str) -> str | None:
        file_candidates = list(filter(lambda s: len(s) > 0 and s[0].isupper(), entity_name.split('.')))
        for c in file_candidates:
            p = f'{self.resolve_dataset_dir(*path_segments, c)}.java'
            if path.exists(p) and path.isfile(p):
                return p
        return None
        

    def resolve_dataset_dir(self, *p: str) -> str:
        return path.join(self.dataset_dir, *p)
    
    def read_source_file_content(self, *path_segments: str, entity_name: str) -> str | None:
        source_file_path = self.resolve_source_file_path(entity_name=entity_name, *path_segments)
        if not source_file_path:
            return None
        src_content = Path(source_file_path).read_bytes().decode('utf-8', 'ignore')
        if not re.search(r'enum\s[A-Z]{1,}', src_content):
            src_content = src_content.replace('enum', 'enumz')
        return src_content


    @classmethod
    def default(cls) -> "Resolver":
        return cls('./dataset', './metrics.csv')
    


class SourceFileParser:
    __node_pool: Dict[str, Node]
    __resolver: Resolver

    def __init__(self, resolver: Resolver) -> None:
        self.__node_pool = {}
        self.__resolver = resolver
    
    def parse_source_file(self, *path_segments: str, entity_name: str) -> Node | None:
        source_file_path = self.__resolver.resolve_source_file_path(entity_name=entity_name, *path_segments)
        if not source_file_path:
             return None
        if source_file_path in self.__node_pool:
            return self.__node_pool[source_file_path]
        source_tree = javalang.parse.parse(self.__resolver.read_source_file_content(entity_name=entity_name, *path_segments))
        self.__node_pool[source_file_path] = source_tree
        return source_tree
    
    def __is_class_or_interface_node(self, n: Node) -> bool:
        is_class = isinstance(n, javalang.tree.ClassDeclaration)
        is_interface = isinstance(n, javalang.tree.InterfaceDeclaration)
        return is_class or is_interface
    
    def __get_entity_name(self, role_entry: RoleEntry) -> str | None:
        if len(role_entry.entity) == 0:
            return None
        classes =  list(filter(lambda s: s[0].isupper(), role_entry.entity.split('.')))
        return classes[-1] if len(classes) else None




    def start_node_generator(self):
        for dp_dir in filter(lambda s: path.isdir(self.__resolver.resolve_dataset_dir(s)), os.listdir(self.__resolver.dataset_dir)):
            logging.info(f'Parsing design pattern {dp_dir}...')
            for micro_arch_dir in os.listdir(path.join(self.__resolver.dataset_dir, dp_dir)):
                logging.info(f'Parsing micro architecture {micro_arch_dir}...')
                role_desc = RoleDescriptor.from_csv(self.__resolver.resolve_dataset_dir(dp_dir, micro_arch_dir))
                for role_entry in role_desc.roleEntries:
                    try:
                        entity_name = self.__get_entity_name(role_entry)
                        if not entity_name:
                            continue
                        source_tree = self.parse_source_file(dp_dir, micro_arch_dir, entity_name=role_entry.entity)
                        if not source_tree:
                            continue
                        for _, n in source_tree:
                            if(self.__is_class_or_interface_node(n) and hasattr(n, 'name') and n.name == entity_name):
                                value = GeneratorValue(n, dp_dir, micro_arch_dir, role_entry)
                                yield GeneratorResult.with_value(value)
                    except JavaSyntaxError as j:
                        yield GeneratorResult.with_error(j.description)
                    except Exception as ex:
                        yield GeneratorResult.with_error(ex.reason)


def get_evaluaters(resolver: Resolver) -> List[class_evaluators.MetricEvaluationInterface]:
    return [class_evaluators.NumberOfFieldsEvaluation()]
   

def parse_arguments() -> Resolver:
    parser = argparse.ArgumentParser(
        prog='Generate software metrics'
    )
    parser.add_argument('--datasetDir', '-d', type=str,
                        help='Path to directory of source files', dest='dataset_dir', required=True)
    parser.add_argument('--outputFile', '-o', type=str, dest='csv_file_path',
                        help='Output path for generated CSV file with calculated software metrics', required=True)
    args = parser.parse_args()
    return Resolver(args.dataset_dir, args.csv_file_path)



def enhance_metrics(value: GeneratorValue, metrics: Dict[str, float]) -> Dict[str, float]:
        metric_entry: Dict[str, str] = {}
        
        metric_entry['role'] = value.role_entry.role
        metric_entry['role_kind'] = value.role_entry.role_kind
        metric_entry['entity'] = value.role_entry.entity
        metric_entry['design_pattern'] = value.dp
        metric_entry['micro_architecture'] = value.micro_arch
        metric_entry = metric_entry | metrics

        return metric_entry
       
         
def main():
    resolver = Resolver.default()
    parser = SourceFileParser(resolver)
    start = time.process_time()
    metric_results = []

    evaluators = get_evaluaters(resolver)
    for result in parser.start_node_generator():
        if result.exception_msg:
            logging.error(result.exception_msg)
            continue
        metrics: Dict[str, float] = {}
        value: GeneratorValue = result.value
        try:
            for e in evaluators:
                metrics[e.get_metric_name()] = e.evaluate(value.node, value.dp, value.micro_arch)
        except Exception as ex:
            metrics[e.get_metric_name()] = 0
            continue
        metric_results.append(enhance_metrics(value, metrics))
    pd.DataFrame(metric_results).to_csv(path_or_buf=resolver.csv_file_path, index=False)
    logging.info(f'Duration: {time.process_time() - start} s')
    
        
        



if __name__ == '__main__':
  """   logging.basicConfig(level='INFO')
    #context = parse_arguments()
    context = Resolver.default()
    start = time.process_time()
    try:
        generate_metrics(context)
        context.save_metrics()
    except Exception as e:
        logging.error(e)
    finally:
        logging.info(f'Duration: {time.process_time() - start} s') """
  logging.basicConfig(level='INFO')
  main()
