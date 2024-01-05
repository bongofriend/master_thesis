package evaluation;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvIgnore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
    private int isPublic;

    @CsvBindByName(column = ClassMetricVectorConstants.IS_ABSTRACT)
    private int isAbstract;

    @CsvBindByName(column = ClassMetricVectorConstants.IS_STATIC)
    private int isStatic;

    @CsvBindByName(column = ClassMetricVectorConstants.EXTENDS_ENTITY)
    private int extendsEntity;

    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_INTERFACES)
    private int countInterfaces;

    @CsvBindByName(column = ClassMetricVectorConstants.EXTENDS_ENTITY_IN_MICRO_ARCHITECTURE)
    private int extendsEntityInMicroArchitecture;

    @CsvBindByName(column = ClassMetricVectorConstants.IS_CLASS)
    private int isClass;

    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_STATIC_FIELDS)
    private int countStaticFields;

    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_PRIVATE_FIELDS)
    private int countPrivateFields;

    //Field Level Metrics
    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_REFERENCE_IN_FIELDS)
    private int countReferenceInFields;

    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_PRIVATE_CONSTRUCTORS)
    private int countPrivateConstructors;

    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_REFERENCE_AS_CONSTRUCTOR_PARAMETER)
    private int countReferenceAsConstructorParameter;

    //Method Level Metrics
    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_STATIC_METHODS)
    private int countStaticMethods;

    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_PRIVATE_METHODS)
    private int countPrivateMethods;

    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_REFERENCE_AS_RETURN_TYPE)
    private int countReferenceAsReturnType;

    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_ABSTRACT_METHODS)
    private int countOfAbstractMethods;

    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_REFERENCE_AS_METHOD_PARAMETER)
    private int countReferenceAsMethodParameter;

    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_REFERENCE_AS_VARIABLE)
    private int countReferenceAsVariable;

    @CsvBindByName(column = ClassMetricVectorConstants.COUNT_OF_REFERENCE_AS_METHOD_INVOCATION)
    private int countReferenceAsMethodInvocation;


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

        //Set constructor level metrics
        this.countPrivateConstructors = metrics.getOrDefault(ClassMetricVectorConstants.COUNT_OF_PRIVATE_CONSTRUCTORS, 0);
        this.countReferenceAsConstructorParameter = metrics.getOrDefault(ClassMetricVectorConstants.COUNT_OF_REFERENCE_AS_CONSTRUCTOR_PARAMETER, 0);

        //Set method level metrics
        this.countStaticMethods = metrics.getOrDefault(ClassMetricVectorConstants.COUNT_OF_STATIC_METHODS, 0);
        this.countPrivateMethods = metrics.getOrDefault(ClassMetricVectorConstants.COUNT_OF_PRIVATE_METHODS, 0);
        this.countOfAbstractMethods = metrics.getOrDefault(ClassMetricVectorConstants.COUNT_OF_ABSTRACT_METHODS, 0);
        this.countReferenceAsReturnType = metrics.getOrDefault(ClassMetricVectorConstants.COUNT_OF_REFERENCE_AS_RETURN_TYPE, 0);
        this.countReferenceAsMethodParameter = metrics.getOrDefault(ClassMetricVectorConstants.COUNT_OF_REFERENCE_AS_METHOD_PARAMETER, 0);
        this.countReferenceAsVariable = metrics.getOrDefault(ClassMetricVectorConstants.COUNT_OF_REFERENCE_AS_VARIABLE, 0);
        this.countReferenceAsMethodInvocation = metrics.getOrDefault(ClassMetricVectorConstants.COUNT_OF_REFERENCE_AS_METHOD_INVOCATION, 0);

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

    public int getCountOfAbstractMethods() {
        return countOfAbstractMethods;
    }

    public void setCountOfAbstractMethods(int countOfAbstractMethods) {
        this.countOfAbstractMethods = countOfAbstractMethods;
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
}
