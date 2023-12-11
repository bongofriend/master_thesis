import abc
from ast import Set
from os import listdir, path
import os
import argparse
import logging
import re
import sys
from source_file_models import RoleDescriptor, RoleEntry
import pandas as pd
from typing import List, Dict, Set
from pathlib import Path
import javalang
from javalang.parser import JavaSyntaxError
from javalang.tree import Node, ClassDeclaration, MethodDeclaration, FieldDeclaration, ReferenceType
import time
from dataclasses import dataclass, field


@dataclass
class GeneratorValue:
    node: Node
    dp: str
    micro_arch: str
    role_entry: RoleEntry
    project: str


@dataclass
class GeneratorResult:
    value: GeneratorValue | None = field(default=None)
    exception_msg: str | None = field(default=None)

    @classmethod
    def with_value(cls, value: GeneratorValue) -> "GeneratorResult":
        return cls(value, None)

    @classmethod
    def with_error(cls, exception_msg: str) -> "GeneratorResult":
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
        file_candidates = list(filter(lambda s: len(
            s) > 0 and s[0].isupper(), entity_name.split('.')))
        for c in file_candidates:
            p = f'{self.resolve_dataset_dir(*path_segments, c)}.java'
            if path.exists(p) and path.isfile(p):
                return p
        return None

    def resolve_dataset_dir(self, *p: str) -> str:
        return path.join(self.dataset_dir, *p)

    def read_source_file_content(self, *path_segments: str, entity_name: str) -> str | None:
        source_file_path = self.resolve_source_file_path(
            entity_name=entity_name, *path_segments)
        if not source_file_path:
            return None
        return self.read_source_file_content_with_path(source_file_path)

    def read_source_file_content_with_path(self, source_file_path: str) -> str | None:
        src_content = Path(source_file_path).read_bytes().decode(
            'utf-8', 'ignore')
        if not re.search(r'enum\s[A-Z]{1,}', src_content):
            src_content = src_content.replace('enum', 'enumz')
        return src_content
    
    def get_project_name(self, micro_arch_dir: str):
        with open(path.join(micro_arch_dir, 'project.txt')) as file:
            return file.readline()

    @classmethod
    def default(cls) -> "Resolver":
        return cls('./dataset', './metrics.csv')


class SourceFileParser:
    __node_pool: Dict[str, Node]
    resolver: Resolver

    def __init__(self, resolver: Resolver) -> None:
        self.__node_pool = {}
        self.resolver = resolver

    def parse_source_file(self, *path_segments: str, entity_name: str) -> Node | None:
        source_file_path = self.resolver.resolve_source_file_path(
            entity_name=entity_name, *path_segments)
        if not source_file_path:
            return None
        return self.parse_source_file_with_path(source_file_path)

    def parse_source_file_with_path(self, source_file_path: str) -> Node | None:
        if source_file_path in self.__node_pool:
            return self.__node_pool[source_file_path]
        source_tree = javalang.parse.parse(
            self.resolver.read_source_file_content_with_path(source_file_path))
        self.__node_pool[source_file_path] = source_tree
        return source_tree

    def __is_class_or_interface_node(self, n: Node) -> bool:
        is_class = isinstance(n, javalang.tree.ClassDeclaration)
        is_interface = isinstance(n, javalang.tree.InterfaceDeclaration)
        return is_class or is_interface

    def __get_entity_name(self, role_entry: RoleEntry) -> str | None:
        if len(role_entry.entity) == 0:
            return None
        classes = list(
            filter(lambda s: s[0].isupper(), role_entry.entity.split('.')))
        return classes[-1] if len(classes) else None

    def start_node_generator(self):
        for dp_dir in filter(lambda s: path.isdir(self.resolver.resolve_dataset_dir(s)), os.listdir(self.resolver.dataset_dir)):
            logging.info(f'Parsing design pattern {dp_dir}...')
            for micro_arch_dir in os.listdir(path.join(self.resolver.dataset_dir, dp_dir)):
                logging.info(f'Parsing micro architecture {micro_arch_dir}...')
                micro_arch_path = self.resolver.resolve_dataset_dir(dp_dir, micro_arch_dir)
                role_desc = RoleDescriptor.from_csv(micro_arch_path)
                project_name = self.resolver.get_project_name(micro_arch_path)
                for role_entry in role_desc.roleEntries:
                    try:
                        entity_name = self.__get_entity_name(role_entry)
                        if not entity_name:
                            continue
                        source_tree = self.parse_source_file(
                            dp_dir, micro_arch_dir, entity_name=role_entry.entity)
                        if not source_tree:
                            continue
                        for _, n in source_tree:
                            if (self.__is_class_or_interface_node(n) and hasattr(n, 'name') and n.name == entity_name):
                                value = GeneratorValue(
                                    n, dp_dir, micro_arch_dir, role_entry, project_name)
                                yield GeneratorResult.with_value(value)
                    except JavaSyntaxError as j:
                        yield GeneratorResult.with_error(j.description)
                    except Exception as ex:
                        yield GeneratorResult.with_error(ex.reason)


class SourceFileMetricEvaulation(metaclass=abc.ABCMeta):
    _parser: SourceFileParser

    @classmethod
    def __subclasshook__(cls, subclass):
        return (
            hasattr(subclass, 'get_metric_name') and callable(subclass.get_metric_name) and
            hasattr(subclass, 'evaluate') and callable(subclass.evaluate) or
            NotImplemented
        )

    def __init__(self, parser: SourceFileParser) -> None:
        self._parser = parser

    @abc.abstractmethod
    def get_metric_name(self) -> str:
        raise NotImplementedError

    @abc.abstractmethod
    def evaluate(self, node: Node, dp_name: str = '', micro_arch: str = '') -> float:
        raise NotImplementedError


class NumberOfFieldsEvaluation(SourceFileMetricEvaulation):

    def __init__(self, parser: SourceFileParser) -> None:
        super().__init__(parser)

    def get_metric_name(self) -> str:
        return 'NOF'

    def evaluate(self, node: Node, dp_name: str = '', micro_arch: str = '') -> float:
        return 0 if not hasattr(node, 'fields') else len(node.fields)


class NumberOfStaticFieldsEvaluation(SourceFileMetricEvaulation):

    def __init__(self, parser: SourceFileParser) -> None:
        super().__init__(parser)

    def get_metric_name(self) -> str:
        return 'NSF'

    def evaluate(self, node: Node, dp_name: str = '', micro_arch: str = '') -> float:
        if not hasattr(node, 'fields') or len(node.fields) == 0:
            return 0
        counter = 0
        for f in node.fields:
            if hasattr(f, 'modifiers') and 'static' in f.modifiers:
                counter += 1
        return counter


class NumberOfMethodsEvaluation(SourceFileMetricEvaulation):

    def __init__(self, parser: SourceFileParser) -> None:
        super().__init__(parser)

    def get_metric_name(self) -> str:
        return 'NOM'

    def evaluate(self, node: Node, dp_name: str = '', micro_arch: str = '') -> float:
        return 0 if not hasattr(node, 'methods') else len(node.methods)


class NumberOfStaticMethodsEvaluation(SourceFileMetricEvaulation):

    def __init__(self, parser: SourceFileParser) -> None:
        super().__init__(parser)

    def get_metric_name(self) -> str:
        return 'NSM'

    def evaluate(self, node: Node, dp_name: str = '', micro_arch: str = '') -> float:
        if not hasattr(node, 'methods') or len(node.methods) == 0:
            return 0

        counter = 0
        for m in node.methods:
            if hasattr(m, 'modifiers') and 'static' in m.modifiers:
                counter += 1
        return counter


class NumberOfInterfacesEvaluation(SourceFileMetricEvaulation):

    def __init__(self, parser: SourceFileParser) -> None:
        super().__init__(parser)

    def get_metric_name(self) -> str:
        return 'NOI'

    def evaluate(self, node: Node, dp_name: str = '', micro_arch: str = '') -> float:
        return 0 if not hasattr(node, 'implements') or node.implements == None else len(node.implements)


class NumberOfAbstractMethodsEvaluation(SourceFileMetricEvaulation):
    def __init__(self, parser: SourceFileParser) -> None:
        super().__init__(parser)

    def get_metric_name(self) -> str:
        return 'NOAM'

    def evaluate(self, node: Node, dp_name: str = '', micro_arch: str = '') -> float:
        if not hasattr(node, 'methods') or len(node.methods) == 0:
            return 0

        counter = 0
        for m in node.methods:
            if hasattr(m, 'modifiers') and 'abstract' in m.modifiers:
                counter += 1
        return counter


@dataclass(unsafe_hash=True)
class MethodParameter:
    name: str
    type: str


@dataclass(unsafe_hash=True)
class MethodReference:
    name: str
    return_type: str
    parameters: Set[MethodParameter] = field(default_factory=set(), hash=True)

    @classmethod
    def from_node(cls, method: javalang.tree.MethodDeclaration):
        method_parameters = frozenset(
            {MethodParameter(p.name, p.type.name) for p in method.parameters})
        return_type = method.return_type.name if method.return_type else 'void'
        return MethodReference(method.name, return_type, method_parameters)


class NumberOfOverriddenMethodsEvaluation(SourceFileMetricEvaulation):

    def __init__(self, parser: SourceFileParser) -> None:
        super().__init__(parser)

    def get_metric_name(self) -> str:
        return 'NORM'

    def evaluate(self, node: Node, dp_name: str = '', micro_arch: str = '') -> float:
        if not hasattr(node, 'methods') or len(node.methods) == 0:
            return 0

        counter = 0
        overriden_methods = self.__get_reference_methods(
            node, dp_name, micro_arch)
        for m in node.methods:
            if MethodReference.from_node(m) in overriden_methods or (hasattr(m, 'annotations') and 'override' in m.annotations):
                counter += 1
        return counter

    def __get_reference_methods(self, node: Node, dp_name: str, mirco_arch: str) -> Set[MethodReference]:
        reference_methods: Set[MethodReference] = set()
        if not self.__has_references(node):
            return reference_methods
        references = self.__get_references(
            node, 'implements') + self.__get_references(node, 'extends')
        reference_paths = map(lambda r: self._parser.resolver.resolve_source_file_path(
            dp_name, mirco_arch, entity_name=r.name), references)
        for r in reference_paths:
            if r is None:
                continue
            reference_class_node = self._parser.parse_source_file_with_path(r)
            for _, n in reference_class_node.filter(javalang.tree.ClassDeclaration):
                for m in n.methods:
                    reference_methods.add(MethodReference.from_node(m))
            for _, n in reference_class_node.filter(javalang.tree.InterfaceDeclaration):
                for m in n.methods:
                    reference_methods.add(MethodReference.from_node(m))
        return frozenset(reference_methods)

    def __has_references(self, node: Node) -> bool:
        has_implement_refs = hasattr(
            node, 'implements') and node.implements != None
        has_extend_refs = hasattr(node, 'extends') and node.extends != None
        return has_implement_refs or has_extend_refs

    def __get_references(self, node: Node, attr_key: str) -> List:
        if not hasattr(node, attr_key) or getattr(node, attr_key) == None:
            return []
        refs = getattr(node, attr_key)
        if isinstance(refs, list):
            return refs
        return [refs]


class NumberOfPrivateConstrcutorsEvaluation(SourceFileMetricEvaulation):
    def __init__(self, parser: SourceFileParser) -> None:
        super().__init__(parser)

    def get_metric_name(self) -> str:
        return 'NOPC'

    def evaluate(self, node: Node, dp_name: str = '', micro_arch: str = '') -> float:
        if not hasattr(node, 'body') or not node.body:
            return 0

        entity_name = node.name
        counter = 0
        for n in node.body:
            if not hasattr(n, 'name') or not n.name:
                continue
            if not hasattr(n, 'modifiers') or not n.modifiers:
                continue
            if n.name == entity_name and 'private' in n.modifiers:
                counter += 1
        return counter


class NumberOfConstructorsWithObjectTypeArgumentEvaluation(SourceFileMetricEvaulation):

    def __init__(self, parser: SourceFileParser) -> None:
        super().__init__(parser)

    def get_metric_name(self) -> str:
        return 'NOTC'

    def evaluate(self, node: Node, dp_name: str = '', micro_arch: str = '') -> float:
        if not hasattr(node, 'body') or not node.body:
            return 0

        entity_name = node.name
        counter = 0
        for n in node.body:
            if not hasattr(n, 'name') or not n.name:
                continue
            if not hasattr(n, 'parameters') or not n.parameters:
                continue
            if n.name == entity_name and self.__has_reference_type_in_parameters(n):
                counter += 1
        return counter

    def __has_reference_type_in_parameters(self, node: Node) -> bool:
        reference_type_parameters = [
            p for p in node.parameters if isinstance(p.type, ReferenceType)]
        return len(reference_type_parameters) > 0


class NumberOfObjectFieldsEvaluation(SourceFileMetricEvaulation):
    
    def __init__(self, parser: SourceFileParser) -> None:
        super().__init__(parser)

    def get_metric_name(self) -> str:
        return 'NOOF'

    def evaluate(self, node: Node, dp_name: str = '', micro_arch: str = '') -> float:
        if not isinstance(node, ClassDeclaration):
            return 0
        if not hasattr(node, 'body') or not node.body:
            return 0
        fields_with_object_type = [f for f in node.body if isinstance(
            f, FieldDeclaration) and isinstance(f.type, ReferenceType)]
        return len(fields_with_object_type)


class NumberOfOtherClassesWithFieldOfOwnTypeEvaluation(SourceFileMetricEvaulation):

    def __init__(self, parser: SourceFileParser) -> None:
        super().__init__(parser)

    def get_metric_name(self) -> str:
        return 'NCOF'

    def evaluate(self, node: Node, dp_name: str = '', micro_arch: str = '') -> float:
        entity_type = node.name
        counter = 0

        for src_file in listdir(self._parser.resolver.resolve_dataset_dir(dp_name, micro_arch)):
            src_file_path: str = self._parser.resolver.resolve_dataset_dir(
                dp_name, micro_arch, src_file)
            if not path.isfile(src_file_path) or not src_file_path.endswith('.java') or src_file == f'{entity_type}.java':
                continue
            source_tree = self._parser.parse_source_file_with_path(
                src_file_path)
            for _, n in source_tree.filter(ClassDeclaration):
                if not hasattr(n, 'fields') or not len(n.fields):
                    continue
                fields_with_reference_type = [f for f in n.fields if isinstance(
                    f, FieldDeclaration) and isinstance(f.type, ReferenceType)]
                for f in fields_with_reference_type:
                    if f.type.name == entity_type:
                        counter += 1
        return counter


def get_evaluaters(parser: SourceFileParser) -> List[SourceFileMetricEvaulation]:
    return [
        NumberOfFieldsEvaluation(parser),
        NumberOfStaticFieldsEvaluation(parser),
        NumberOfMethodsEvaluation(parser),
        NumberOfStaticMethodsEvaluation(parser),
        NumberOfInterfacesEvaluation(parser),
        NumberOfAbstractMethodsEvaluation(parser),
        NumberOfOverriddenMethodsEvaluation(parser),
        NumberOfPrivateConstrcutorsEvaluation(parser),
        NumberOfConstructorsWithObjectTypeArgumentEvaluation(parser),
        NumberOfObjectFieldsEvaluation(parser),
        NumberOfOtherClassesWithFieldOfOwnTypeEvaluation(parser)

    ]


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
    metric_entry['project'] = value.project
    metric_entry = metric_entry | metrics

    return metric_entry


def main():
    resolver = Resolver.default()
    parser = SourceFileParser(resolver)
    start = time.process_time()
    metric_results = []

    evaluators = get_evaluaters(parser)
    for result in parser.start_node_generator():
        if result.exception_msg:
            logging.error(result.exception_msg)
            continue
        metrics: Dict[str, float] = {}
        value: GeneratorValue = result.value
        try:
            for e in evaluators:
                metrics[e.get_metric_name()] = e.evaluate(
                    value.node, value.dp, value.micro_arch)
        except Exception as ex:
            metrics[e.get_metric_name()] = 0
            continue
        metric_results.append(enhance_metrics(value, metrics))
    pd.DataFrame(metric_results).to_csv(
        path_or_buf=resolver.csv_file_path, index=False)
    logging.info(f'Duration: {time.process_time() - start} s')


if __name__ == '__main__':
    logging.basicConfig(level='INFO')
    main()
