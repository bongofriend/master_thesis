import abc
from javalang.tree import Node


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
    def evaluate(self, node: Node) -> float:
        raise NotImplementedError


class NumberOfFieldsEvaluation(MetricEvaluationInterface):
    def get_metric_name(self) -> str:
        return 'NOF'

    def evaluate(self, node: Node) -> float:
       return 0 if not hasattr(node, 'fields') else len(node.fields)

class NumberOfStaticFieldsEvaluation(MetricEvaluationInterface):
    def get_metric_name(self) -> str:
        return 'NSF'
    
    def evaluate(self, node: Node) -> float:
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
    
    def evaluate(self, node: Node) -> float:
        return 0 if not hasattr(node, 'methods') else len(node.methods)
class NumberOfStaticMethodsEvaluation(MetricEvaluationInterface):
    def get_metric_name(self) -> str:
        return 'NSM'

    def evaluate(self, node: Node) -> float:
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
    
    def evaluate(self, node: Node) -> float:
        return 0 if not hasattr(node, 'implements') or node.implements == None else len(node.implements)

class NumberOfAbstractMethodsEvaluation(MetricEvaluationInterface):
    def get_metric_name(self) -> str:
        return 'NOAM'
    
    def evaluate(self, node: Node) -> float:
        if not hasattr(node, 'methods') or len(node.methods) == 0:
            return 0
        
        counter = 0
        for m in node.methods:
            if hasattr(m, 'modifiers') and 'abstract' in m.modifiers:
                counter += 1
        return counter

class NumberOfOverriddenMethodsEvaluation(MetricEvaluationInterface):
    def get_metric_name(self) -> str:
        return 'NORM'
    
    def evaluate(self, node: Node) -> float:
        if not hasattr(node, 'methods') or len(node.methods) == 0:
            return 0
        
        counter = 0
        for m in node.methods:
            if hasattr(m, 'annotations') and 'override' in m.annotations:
                counter += 1
        return counter

class NumberOfPrivateConstrcutorsEvaluation(MetricEvaluationInterface):
    def get_metric_name(self) -> str:
        return 'NOPC'
    
    def evaluate(self, node: Node) -> float:
        if not hasattr(node, 'constructors') or len(node.constructors) == 0:
            return 0
        
        counter = 0
        for c in node.constructors:
            if hasattr(c, 'modifiers') and 'private' in c.modifiers:
                counter += 1
        return counter

    def __is_override(node: Node, field: str) -> bool:
        #TODO: @Override decorator not available; Check for implicit overrides in implemented interfaces or extended classes
        pass



