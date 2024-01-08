package evaluation;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvIgnore;

import java.util.HashMap;
import java.util.Map;

public class ClassMetricVector {

    //Design Pattern Meta Data
    @CsvBindByName(column = ClassMetricVectorConstants.ROLE)
    public String role;
    @CsvBindByName(column = ClassMetricVectorConstants.ROLE_KIND)
    private String roleKind;
    @CsvBindByName(column = ClassMetricVectorConstants.ENTITY)
    private String entity;
    @CsvBindByName(column = ClassMetricVectorConstants.DESIGN_PATTERN)
    private String designPattern;
    @CsvBindByName(column = ClassMetricVectorConstants.MICRO_ARCHITECTURE)
    private String microArchitecture;
    @CsvBindByName(column = ClassMetricVectorConstants.PROJECT)
    private String project;

    @CsvIgnore
    private final Map<String, Integer> metrics;

    public ClassMetricVector(String role, String roleKind, String entity, String designPattern, String microArchitecture, String project) {
        this.role = role;
        this.roleKind = roleKind;
        this.entity = entity;
        this.designPattern = designPattern;
        this.microArchitecture = microArchitecture;
        this.project = project;
        this.metrics = new HashMap<>();
    }

    //Class Level Metrics
    @CsvBindByName(column = ClassMetricVectorConstants.IS_PUBLIC)
    @CsvIgnore
    private int isPublic;

    @CsvBindByName(column = ClassMetricVectorConstants.IS_ABSTRACT)
    @CsvIgnore
    private int isAbstract;

    @CsvBindByName(column = ClassMetricVectorConstants.IS_STATIC)
    @CsvIgnore
    private int isStatic;

    @CsvBindByName(column = ClassMetricVectorConstants.EXTENDS_ENTITY)
    @CsvIgnore
    private int extendsEntity;

    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_INTERFACES)
    @CsvIgnore
    private int countInterfaces;

    @CsvBindByName(column = ClassMetricVectorConstants.EXTENDS_ENTITY_IN_MICRO_ARCHITECTURE)
    @CsvIgnore
    private int extendsEntityInMicroArchitecture;

    @CsvBindByName(column = ClassMetricVectorConstants.IS_CLASS)
    @CsvIgnore
    private int isClass;

    //Field Level Metrics
    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_REFERENCE_IN_FIELDS)
    @CsvIgnore
    private int countReferenceInFields;

    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_STATIC_FIELDS)
    private int countStaticFields;

    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_FIELDS)
    private int countFields;

    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_OBJECT_FIELDS)
    public int countObjectFields;
    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_OTHER_CLASSES_WITH_FIELD_OF_OWN_TYPE)
    private int countOtherClassesWithFieldOfOwnType;

    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_PRIVATE_FIELDS)
    @CsvIgnore
    private int countPrivateFields;

    //Constructor Level Metrics
    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_PRIVATE_CONSTRUCTORS)
    private int countPrivateConstructors;

    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_REFERENCE_AS_CONSTRUCTOR_PARAMETER)
    @CsvIgnore
    private int countReferenceAsConstructorParameter;

    //Method Level Metrics
    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_STATIC_METHODS)
    private int countStaticMethods;

    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_PRIVATE_METHODS)
    @CsvIgnore
    private int countPrivateMethods;

    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_REFERENCE_AS_RETURN_TYPE)
    @CsvIgnore
    private int countReferenceAsReturnType;

    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_ABSTRACT_METHODS)
    private int countAbstractMethods;

    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_REFERENCE_AS_METHOD_PARAMETER)
    @CsvIgnore
    private int countReferenceAsMethodParameter;

    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_REFERENCE_AS_VARIABLE)
    @CsvIgnore
    private int countReferenceAsVariable;

    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_REFERENCE_AS_METHOD_INVOCATION)
    @CsvIgnore
    private int countReferenceAsMethodInvocation;

    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_METHODS)
    private int countMethods;

    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_OVERRIDDEN_METHODS)
    private int countOverriddenMethods;


    public void addMetric(String metricColumnName, int value) {
        this.metrics.put(metricColumnName, value);
    }

    public void prepare() {
        //Set class level metrics
        this.isPublic = metrics.getOrDefault(ClassMetricVectorConstants.IS_PUBLIC, 0);
        this.isAbstract = metrics.getOrDefault(ClassMetricVectorConstants.IS_ABSTRACT, 0);
        this.isStatic = metrics.getOrDefault(ClassMetricVectorConstants.IS_STATIC, 0);
        this.extendsEntity = metrics.getOrDefault(ClassMetricVectorConstants.EXTENDS_ENTITY, 0);
        this.countInterfaces = metrics.getOrDefault(ClassMetricVectorConstants.COUNT_OF_INTERFACES, 0);
        this.isClass = metrics.getOrDefault(ClassMetricVectorConstants.IS_CLASS, 0);
        this.extendsEntityInMicroArchitecture = metrics.getOrDefault(ClassMetricVectorConstants.EXTENDS_ENTITY_IN_MICRO_ARCHITECTURE, 0);

        //Set field level metrics
        this.countStaticFields = metrics.getOrDefault(ClassMetricVectorConstants.COUNT_OF_STATIC_FIELDS, 0);
        this.countPrivateFields = metrics.getOrDefault(ClassMetricVectorConstants.COUNT_OF_PRIVATE_FIELDS, 0);
        this.countReferenceInFields = metrics.getOrDefault(ClassMetricVectorConstants.COUNT_OF_REFERENCE_IN_FIELDS, 0);
        this.countFields = metrics.getOrDefault(ClassMetricVectorConstants.COUNT_OF_FIELDS, 0);
        this.countObjectFields = metrics.getOrDefault(ClassMetricVectorConstants.COUNT_OF_OBJECT_FIELDS, 0);
        this.countOtherClassesWithFieldOfOwnType = metrics.getOrDefault(ClassMetricVectorConstants.COUNT_OF_OTHER_CLASSES_WITH_FIELD_OF_OWN_TYPE, 0);

        //Set constructor level metrics
        this.countPrivateConstructors = metrics.getOrDefault(ClassMetricVectorConstants.COUNT_OF_PRIVATE_CONSTRUCTORS, 0);
        this.countReferenceAsConstructorParameter = metrics.getOrDefault(ClassMetricVectorConstants.COUNT_OF_REFERENCE_AS_CONSTRUCTOR_PARAMETER, 0);

        //Set method level metrics
        this.countStaticMethods = metrics.getOrDefault(ClassMetricVectorConstants.COUNT_OF_STATIC_METHODS, 0);
        this.countPrivateMethods = metrics.getOrDefault(ClassMetricVectorConstants.COUNT_OF_PRIVATE_METHODS, 0);
        this.countAbstractMethods = metrics.getOrDefault(ClassMetricVectorConstants.COUNT_OF_ABSTRACT_METHODS, 0);
        this.countReferenceAsReturnType = metrics.getOrDefault(ClassMetricVectorConstants.COUNT_OF_REFERENCE_AS_RETURN_TYPE, 0);
        this.countReferenceAsMethodParameter = metrics.getOrDefault(ClassMetricVectorConstants.COUNT_OF_REFERENCE_AS_METHOD_PARAMETER, 0);
        this.countReferenceAsVariable = metrics.getOrDefault(ClassMetricVectorConstants.COUNT_OF_REFERENCE_AS_VARIABLE, 0);
        this.countReferenceAsMethodInvocation = metrics.getOrDefault(ClassMetricVectorConstants.COUNT_OF_REFERENCE_AS_METHOD_INVOCATION, 0);
        this.countMethods = metrics.getOrDefault(ClassMetricVectorConstants.COUNT_OF_METHODS, 0);
        this.countOverriddenMethods = metrics.getOrDefault(ClassMetricVectorConstants.COUNT_OF_OVERRIDDEN_METHODS, 0);

    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRoleKind() {
        return roleKind;
    }

    public void setRoleKind(String roleKind) {
        this.roleKind = roleKind;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getDesignPattern() {
        return designPattern;
    }

    public void setDesignPattern(String designPattern) {
        this.designPattern = designPattern;
    }

    public String getMicroArchitecture() {
        return microArchitecture;
    }

    public void setMicroArchitecture(String microArchitecture) {
        this.microArchitecture = microArchitecture;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public int getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(int isPublic) {
        this.isPublic = isPublic;
    }

    public int getIsAbstract() {
        return isAbstract;
    }

    public void setIsAbstract(int isAbstract) {
        this.isAbstract = isAbstract;
    }

    public int getIsStatic() {
        return isStatic;
    }

    public void setIsStatic(int isStatic) {
        this.isStatic = isStatic;
    }

    public int getExtendsEntity() {
        return extendsEntity;
    }

    public void setExtendsEntity(int extendsEntity) {
        this.extendsEntity = extendsEntity;
    }

    public int getCountInterfaces() {
        return countInterfaces;
    }

    public void setCountInterfaces(int countInterfaces) {
        this.countInterfaces = countInterfaces;
    }

    public int getExtendsEntityInMicroArchitecture() {
        return extendsEntityInMicroArchitecture;
    }

    public void setExtendsEntityInMicroArchitecture(int extendsEntityInMicroArchitecture) {
        this.extendsEntityInMicroArchitecture = extendsEntityInMicroArchitecture;
    }

    public int getIsClass() {
        return isClass;
    }

    public void setIsClass(int isClass) {
        this.isClass = isClass;
    }

    public int getCountStaticFields() {
        return countStaticFields;
    }

    public void setCountStaticFields(int countStaticFields) {
        this.countStaticFields = countStaticFields;
    }

    public int getCountPrivateFields() {
        return countPrivateFields;
    }

    public void setCountPrivateFields(int countPrivateFields) {
        this.countPrivateFields = countPrivateFields;
    }

    public int getCountReferenceInFields() {
        return countReferenceInFields;
    }

    public void setCountReferenceInFields(int countReferenceInFields) {
        this.countReferenceInFields = countReferenceInFields;
    }

    public int getCountPrivateConstructors() {
        return countPrivateConstructors;
    }

    public void setCountPrivateConstructors(int countPrivateConstructors) {
        this.countPrivateConstructors = countPrivateConstructors;
    }

    public int getCountReferenceAsConstructorParameter() {
        return countReferenceAsConstructorParameter;
    }

    public void setCountReferenceAsConstructorParameter(int countReferenceAsConstructorParameter) {
        this.countReferenceAsConstructorParameter = countReferenceAsConstructorParameter;
    }

    public int getCountStaticMethods() {
        return countStaticMethods;
    }

    public void setCountStaticMethods(int countStaticMethods) {
        this.countStaticMethods = countStaticMethods;
    }

    public int getCountPrivateMethods() {
        return countPrivateMethods;
    }

    public int getCountAbstractMethods() {
        return countAbstractMethods;
    }

    public void setCountAbstractMethods(int countAbstractMethods) {
        this.countAbstractMethods = countAbstractMethods;
    }

    public void setCountPrivateMethods(int countPrivateMethods) {
        this.countPrivateMethods = countPrivateMethods;
    }

    public int getCountReferenceAsReturnType() {
        return countReferenceAsReturnType;
    }

    public void setCountReferenceAsReturnType(int countReferenceAsReturnType) {
        this.countReferenceAsReturnType = countReferenceAsReturnType;
    }

    public int getCountReferenceAsMethodParameter() {
        return countReferenceAsMethodParameter;
    }

    public void setCountReferenceAsMethodParameter(int countReferenceAsMethodParameter) {
        this.countReferenceAsMethodParameter = countReferenceAsMethodParameter;
    }

    public int getCountReferenceAsVariable() {
        return countReferenceAsVariable;
    }

    public void setCountReferenceAsVariable(int countReferenceAsVariable) {
        this.countReferenceAsVariable = countReferenceAsVariable;
    }

    public int getCountReferenceAsMethodInvocation() {
        return countReferenceAsMethodInvocation;
    }

    public void setCountReferenceAsMethodInvocation(int countReferenceAsMethodInvocation) {
        this.countReferenceAsMethodInvocation = countReferenceAsMethodInvocation;
    }

    public int getCountFields() {
        return countFields;
    }

    public void setCountFields(int countFields) {
        this.countFields = countFields;
    }

    public int getCountObjectFields() {
        return countObjectFields;
    }

    public void setCountObjectFields(int countObjectFields) {
        this.countObjectFields = countObjectFields;
    }

    public int getCountOtherClassesWithFieldOfOwnType() {
        return countOtherClassesWithFieldOfOwnType;
    }

    public void setCountOtherClassesWithFieldOfOwnType(int countOtherClassesWithFieldOfOwnType) {
        this.countOtherClassesWithFieldOfOwnType = countOtherClassesWithFieldOfOwnType;
    }

    public int getCountMethods() {
        return countMethods;
    }

    public void setCountMethods(int countMethods) {
        this.countMethods = countMethods;
    }

    public int getCountOverriddenMethods() {
        return countOverriddenMethods;
    }

    public void setCountOverriddenMethods(int countOverriddenMethods) {
        this.countOverriddenMethods = countOverriddenMethods;
    }
}
