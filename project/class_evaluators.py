import abc
from javalang.tree import Node, MethodDeclaration
from typing import Dict, List, Set
#from generate_metrics import Context
import pathlib
import javalang
from os import path
from dataclasses import dataclass, field
from collections import namedtuple

class MetricEvaluationInterface(metaclass=abc.ABCMeta):

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
    def evaluate(self, node: Node, dp_name: str = '', micro_arch: str = '') -> float:
        raise NotImplementedError


class NumberOfFieldsEvaluation(MetricEvaluationInterface):
    def get_metric_name(self) -> str:
        return 'NOF'

    def evaluate(self, node: Node, dp_name: str = '', micro_arch: str = '') -> float:
       return 0 if not hasattr(node, 'fields') else len(node.fields)

class NumberOfStaticFieldsEvaluation(MetricEvaluationInterface):
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

class NumberOfMethodsEvaluation(MetricEvaluationInterface):
    def get_metric_name(self) -> str:
        return 'NOM'
    
    def evaluate(self, node: Node, dp_name: str = '', micro_arch: str = '') -> float:
        return 0 if not hasattr(node, 'methods') else len(node.methods)

class NumberOfStaticMethodsEvaluation(MetricEvaluationInterface):
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
    
class NumberOfInterfacesEvaluation(MetricEvaluationInterface):
    def get_metric_name(self) -> str:
        return 'NOI'
    
    def evaluate(self, node: Node, dp_name: str = '', micro_arch: str = '') -> float:
        return 0 if not hasattr(node, 'implements') or node.implements == None else len(node.implements)

class NumberOfAbstractMethodsEvaluation(MetricEvaluationInterface):
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
    def from_node(cls, method: MethodDeclaration) :
        method_parameters = frozenset({ MethodParameter(p.name, p.type.name) for p in method.parameters })
        return_type = method.return_type.name if method.return_type else 'void'
        return MethodReference(method.name, return_type, method_parameters)
            

class NumberOfOverriddenMethodsEvaluation(MetricEvaluationInterface):
    #__context


    def __init__(self, context):
        self.__context = context

    def get_metric_name(self) -> str:
        return 'NORM'
    
    def evaluate(self, node: Node, dp_name: str = '', micro_arch: str = '') -> float:
        if not hasattr(node, 'methods') or len(node.methods) == 0:
            return 0
        
        counter = 0
        overriden_methods = self.__get_reference_methods(node, dp_name, micro_arch)
        for m in node.methods:
            if MethodReference.from_node(m) in overriden_methods or (hasattr(m, 'annotations') and 'override' in m.annotations):
                counter += 1
        return counter
    
    #TODO: Move some methods to paranet base class
    def __get_reference_methods(self, node: Node, dp_name: str, mirco_arch: str) -> Set[MethodReference]:
        reference_methods: Set[MethodReference] = set()
        if not self.__has_references(node):
            return reference_methods
        references = self.__get_references(node, 'implements') + self.__get_references(node, 'extends')
        reference_paths = map(lambda r: self.__get_source_file_path(r.name, dp_name, mirco_arch), references)
        for r in reference_paths:
            if r is None:
                continue
            reference_class_node = javalang.parse.parse(pathlib.Path(r).read_text())
            for _, n in reference_class_node.filter(javalang.tree.ClassDeclaration):
                for m in n.methods:
                    #reference_methods.add(m.name)
                    reference_methods.add(MethodReference.from_node(m))
            for _, n in reference_class_node.filter(javalang.tree.InterfaceDeclaration):
                for m in n.methods:
                    #reference_methods.add(m.name)
                    reference_methods.add(MethodReference.from_node(m))
        return frozenset(reference_methods)
    
    def __has_references(self, node: Node) -> bool:
        has_implement_refs = hasattr(node, 'implements') and node.implements != None
        has_extend_refs = hasattr(node, 'extends') and node.extends != None
        return has_implement_refs or has_extend_refs

    def __get_references(self, node: Node, attr_key: str) -> List:
        if not hasattr(node, attr_key) or getattr(node, attr_key) == None:
            return []
        refs = getattr(node, attr_key)
        if isinstance(refs, list):
            return refs
        return [refs]


    def __get_source_file_path(self, entity_name: str, dp_name: str, micro_arch: str) -> str:
        file_candidates = list(filter(lambda s: len(
        s) > 0 and s[0].isupper(), entity_name.split('.')))
        for c in file_candidates:
            p = f'{self.__context.resolve_dataset_dir(dp_name, micro_arch, c)}.java'
            if path.exists(p) and path.isfile(p):
                return p
        return None

#TODO: Constructors not marked, search manually for node points
class NumberOfPrivateConstrcutorsEvaluation(MetricEvaluationInterface):
    def get_metric_name(self) -> str:
        return 'NOPC'
    
    def evaluate(self, node: Node, dp_name: str = '', micro_arch: str = '') -> float:
        if not hasattr(node, 'constructors') or len(node.constructors) == 0:
            return 0
        
        counter = 0
        for c in node.constructors:
            if hasattr(c, 'modifiers') and 'private' in c.modifiers:
                counter += 1
        return counter


#TODO: Implement rest of metrics

