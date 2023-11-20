import abc
from javalang.tree import Node



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
    def evaluate(self, node: Node) -> float:
        raise NotImplementedError


class FirstTestEvaluator(MeticEvaluaterInterface):

    def get_metric_name(self) -> str:
        return 'Test1'

    def evaluate(self, node: Node) -> float:
        return 0

class SecondTestEvaluator(MeticEvaluaterInterface):

    def get_metric_name(self) -> str:
        return 'Test2'
    
    def evaluate(self, node: Node) -> float:
        return -1