class ClassMetricVectorConstants:
    # Class level
    IS_PUBLIC = "IS_PUBLIC"
    IS_ABSTRACT = "IS_ABSTRACT"
    IS_STATIC = "IS_STATIC"
    EXTENDS_ENTITY = "EXTENDS_ENTITY"
    COUNT_OF_INTERFACES = "COUNT_OF_INTERFACES"
    IS_CLASS = "IS_CLASS"
    EXTENDS_ENTITY_IN_MICRO_ARCHITECTURE = "EXTENDS_ENTITY_IN_MICRO_ARCHITECTURE"
    # Field level
    COUNT_OF_STATIC_FIELDS = "COUNT_OF_STATIC_FIELDS"
    COUNT_OF_PRIVATE_FIELDS = "COUNT_OF_PRIVATE_FIELDS"
    COUNT_OF_REFERENCE_IN_FIELDS = "COUNT_OF_REFERENCE_IN_FIELDS"
    # Constructor level
    COUNT_OF_PRIVATE_CONSTRUCTORS = "COUNT_OF_PRIVATE_CONSTRUCTORS"
    COUNT_OF_REFERENCE_AS_CONSTRUCTOR_PARAMETER = "COUNT_OF_REFERENCE_AS_CONSTRUCTOR_PARAMETER"
    # Method level
    COUNT_OF_STATIC_METHODS = "COUNT_OF_STATIC_METHODS"
    COUNT_OF_PRIVATE_METHODS = "COUNT_OF_PRIVATE_METHODS"
    COUNT_OF_REFERENCE_AS_RETURN_TYPE = "COUNT_OF_REFERENCE_AS_RETURN_TYPE"
    COUNT_OF_REFERENCE_AS_METHOD_PARAMETER = "COUNT_OF_REFERENCE_AS_METHOD_PARAMETER"
    COUNT_OF_REFERENCE_AS_VARIABLE = "COUNT_OF_REFERENCE_AS_VARIABLE"
    COUNT_OF_REFERENCE_AS_METHOD_INVOCATION = "COUNT_OF_REFERENCE_AS_METHOD_INVOCATION"
    COUNT_OF_ABSTRACT_METHODS = "COUNT_OF_ABSTRACT_METHODS"
    COUNT_OF_FIELDS = "COUNT_OF_FIELDS"
    COUNT_OF_METHODS = "COUNT_OF_METHODS"
    COUNT_OF_OBJECT_FIELDS = "COUNT_OF_OBJECT_FIELDS"
    COUNT_OF_OTHER_CLASSES_WITH_FIELD_OF_OWN_TYPE = "COUNT_OF_OTHER_CLASSES_WITH_FIELD_OF_OWN_TYPE"
    COUNT_OF_OVERRIDDEN_METHODS = "COUNT_OF_OVERRIDDEN_METHODS"
    COUPLING_BETWEEN_OBJECTS = "COUPLING_BETWEEN_OBJECTS"
    WEIGHTED_METHOD_CLASS = "WEIGHTED_METHOD_CLASS"
    TIGHT_CLASS_COHESION = "TIGHT_CLASS_COHESION"
    DEPTH_OF_INHERITANCE = "DEPTH_OF_INHERITANCE"
    RESPONSE_FOR_A_CLASS = "RESPONSE_FOR_A_CLASS"
    LACK_OF_COHESION_OF_METHODS = "LACK_OF_COHESION_OF_METHODS"
    ROLE = "role"
    ROLE_KIND = "role_kind"
    ENTITY = "entity"
    DESIGN_PATTERN = "design_pattern"
    MICRO_ARCHITECTURE = "micro_architecture"
    PROJECT = "project"

    def __init__(self):
        # Make the class uninstantiable by raising an exception in the constructor
        raise NotImplementedError("This class should not be instantiated.")


def get_label_column():
    return [ClassMetricVectorConstants.ROLE]


def get_metric_columns():
    return [
        ClassMetricVectorConstants.COUNT_OF_ABSTRACT_METHODS,
        ClassMetricVectorConstants.COUNT_OF_FIELDS,
        ClassMetricVectorConstants.COUNT_OF_INTERFACES,
        ClassMetricVectorConstants.COUNT_OF_METHODS,
        ClassMetricVectorConstants.COUNT_OF_OBJECT_FIELDS,
        ClassMetricVectorConstants.COUNT_OF_OTHER_CLASSES_WITH_FIELD_OF_OWN_TYPE,
        ClassMetricVectorConstants.COUNT_OF_OVERRIDDEN_METHODS,
        ClassMetricVectorConstants.COUNT_OF_PRIVATE_CONSTRUCTORS,
        ClassMetricVectorConstants.COUNT_OF_PRIVATE_FIELDS,
        ClassMetricVectorConstants.COUNT_OF_PRIVATE_METHODS,
        ClassMetricVectorConstants.COUNT_OF_STATIC_FIELDS,
        ClassMetricVectorConstants.COUNT_OF_STATIC_METHODS,
        ClassMetricVectorConstants.COUPLING_BETWEEN_OBJECTS,
        ClassMetricVectorConstants.DEPTH_OF_INHERITANCE,
        ClassMetricVectorConstants.IS_ABSTRACT,
        # ClassMetricVectorConstants.IS_CLASS,
        ClassMetricVectorConstants.IS_PUBLIC,
        # ClassMetricVectorConstants.IS_STATIC,
        ClassMetricVectorConstants.LACK_OF_COHESION_OF_METHODS,
        ClassMetricVectorConstants.RESPONSE_FOR_A_CLASS,
        ClassMetricVectorConstants.TIGHT_CLASS_COHESION,
        ClassMetricVectorConstants.WEIGHTED_METHOD_CLASS
    ]
