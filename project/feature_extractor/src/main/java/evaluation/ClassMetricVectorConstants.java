package evaluation;

public class ClassMetricVectorConstants {
    //Class level
    public static final String IS_PUBLIC = "is_public";
    public static final String IS_ABSTRACT = "is_abstract";
    public static final String IS_STATIC = "is_static";
    @Deprecated
    public static final String EXTENDS_ENTITY = "extends_entity";
    public static final String COUNT_OF_INTERFACES = "count_of_interfaces";
    public static final String IS_CLASS = "is_class";
    @Deprecated
    public static final String EXTENDS_ENTITY_IN_MICRO_ARCHITECTURE = "extends_entity_in_micro_architecture";
    //Field level
    public static final String COUNT_OF_STATIC_FIELDS = "count_of_static_fields";
    public static final String COUNT_OF_PRIVATE_FIELDS = "count_of_private_fields";
    @Deprecated
    public static final String COUNT_OF_REFERENCE_IN_FIELDS = "count_of_reference_in_fields";
    //Constructor level
    public static final String COUNT_OF_PRIVATE_CONSTRUCTORS = "count_of_private_constructors";
    @Deprecated
    public static final String COUNT_OF_REFERENCE_AS_CONSTRUCTOR_PARAMETER = "count_of_reference_as_constructor_parameter";
    //Method level
    public static final String COUNT_OF_STATIC_METHODS = "count_of_static_methods";
    public static final String COUNT_OF_PRIVATE_METHODS = "count_of_private_methods";
    @Deprecated
    public static final String COUNT_OF_REFERENCE_AS_RETURN_TYPE = "count_of_reference_as_return_type";
    @Deprecated
    public static final String COUNT_OF_REFERENCE_AS_METHOD_PARAMETER = "count_of_reference_as_method_parameter";
    @Deprecated
    public static final String COUNT_OF_REFERENCE_AS_VARIABLE = "count_of_reference_as_variable";
    @Deprecated
    public static final String COUNT_OF_REFERENCE_AS_METHOD_INVOCATION = "count_of_reference_as_method_invocation";
    public static final String COUNT_OF_ABSTRACT_METHODS = "count_of_abstract_methods";
    public static final String COUNT_OF_FIELDS = "count_of_fields";
    public static final String COUNT_OF_METHODS = "count_of_methods";
    public static final String COUNT_OF_OBJECT_FIELDS = "count_of_object_fields";
    public static final String COUNT_OF_OTHER_CLASSES_WITH_FIELD_OF_OWN_TYPE = "count_of_other_classes_with_field_of_own_type";
    public static final String COUNT_OF_OVERRIDDEN_METHODS = "count_of_overridden_methods";
    public static final String COUPLING_BETWEEN_OBJECTS = "coupling_between_objects";
    public static final String WEIGHTED_METHOD_CLASS = "weighted_method_class";
    public static final String TIGHT_CLASS_COHESION = "tight_class_cohesion";
    public static final String DEPTH_OF_INHERITANCE = "depth_of_inheritance";
    public static final String RESPONSE_FOR_A_CLASS = "response_for_a_class";
    public static final String LACK_OF_COHESION_OF_METHODS = "lack_of_cohesion_of_methods";
    public static final String ROLE = "role";
    public static final String ROLE_KIND = "role_kind";
    public static final String ENTITY = "entity";
    public static final String DESIGN_PATTERN = "design_pattern";
    public static final String MICRO_ARCHITECTURE = "micro_architecture";
    public static final String PROJECT = "project";
    private ClassMetricVectorConstants() {
    }

}
