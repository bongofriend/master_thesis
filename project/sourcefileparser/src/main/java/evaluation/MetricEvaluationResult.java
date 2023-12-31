package evaluation;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MetricEvaluationResult {

    @CsvBindByName(column = MetricEvaluationResultConstants.ROLE)
    public String role;
    @CsvBindByName(column = MetricEvaluationResultConstants.ROLE_KIND)
    private String roleKind;
    @CsvBindByName(column = MetricEvaluationResultConstants.ENTITY)
    private String entity;
    @CsvBindByName(column = MetricEvaluationResultConstants.DESIGN_PATTERN)
    private String designPattern;
    @CsvBindByName(column = MetricEvaluationResultConstants.MICRO_ARCHITECTURE)
    private String microArchitecture;
    @CsvBindByName(column = MetricEvaluationResultConstants.PROJECT)
    private String project;

    //Number of Fields
    @CsvBindByName(column = MetricEvaluationResultConstants.NOF)
    private float NOF;

    //Number of static Fields
    @CsvBindByName(column = MetricEvaluationResultConstants.NSF)
    private float NSF;
    //Number of Methods
    @CsvBindByName(column = MetricEvaluationResultConstants.NOM)
    private float NOM;

    //Number of Static Methods
    @CsvBindByName(column = MetricEvaluationResultConstants.NSM)
    private float NSM;

    //Number of Abstract Methods
    @CsvBindByName(column = MetricEvaluationResultConstants.NOAM)
    private float NOAM;

    //Number of Private Constructors
    @CsvBindByName(column = MetricEvaluationResultConstants.NOPC)
    private float NOPC;

    //Number of Overridden Methods
    @CsvBindByName(column = MetricEvaluationResultConstants.NORM)
    private float NORM;

    //Number of Object Fields
    @CsvBindByName(column = MetricEvaluationResultConstants.NOOF)
    private float NOOF;

    //Number of Other Classes With Field Of Own Type
    //TODO: Same as FAN_OUT?
    @CsvBindByName(column = MetricEvaluationResultConstants.NCOF)
    private float NCOF;

    //Coupling between objects
    @CsvBindByName(column = MetricEvaluationResultConstants.CBO)
    private float CBO;

    //Number of Input Dependencies
    @CsvBindByName(column = MetricEvaluationResultConstants.FAN_IN)
    private float FAN_IN;

    //Number of Output Dependencies
    @CsvBindByName(column = MetricEvaluationResultConstants.FAN_OUT)
    private float FAN_OUT;

    //Number of Children
    @CsvBindByName(column = MetricEvaluationResultConstants.NOC)
    private float NOC;

    //Response for a class
    @CsvBindByName(column = MetricEvaluationResultConstants.RFC)
    private float RFC;

    //Tight Class Cohesion
    @CsvBindByName(column = MetricEvaluationResultConstants.TCC)
    private float TCC;

    //Loose Class Cohesion
    @CsvBindByName(column = MetricEvaluationResultConstants.LCC)
    private float LCC;

    public String getMeanVector() {
        return meanVector;
    }

    public void setMeanVector(String meanVector) {
        this.meanVector = meanVector;
    }

    public float getComponentMean() {
        return componentMean;
    }

    public void setComponentMean(float componentMean) {
        this.componentMean = componentMean;
    }

    @CsvBindByName(column = MetricEvaluationResultConstants.MEAN_VECTOR)
    private String meanVector;

    @CsvBindByName(column = MetricEvaluationResultConstants.COMPONENT_MEAN)
    private float componentMean;


    public MetricEvaluationResult() {
    }


    public float getNOC() {
        return NOC;
    }

    public void setNOC(float NOC) {
        this.NOC = NOC;
    }

    public float getRFC() {
        return RFC;
    }

    public void setRFC(float RFC) {
        this.RFC = RFC;
    }

    public float getTCC() {
        return TCC;
    }

    public void setTCC(float TCC) {
        this.TCC = TCC;
    }

    public float getLCC() {
        return LCC;
    }

    public void setLCC(float LCC) {
        this.LCC = LCC;
    }

    public float getNSM() {
        return NSM;
    }

    public void setNSM(float NSM) {
        this.NSM = NSM;
    }

    public float getCBO() {
        return CBO;
    }

    public void setCBO(float CBO) {
        this.CBO = CBO;
    }

    public float getFAN_IN() {
        return FAN_IN;
    }

    public void setFAN_IN(float FAN_IN) {
        this.FAN_IN = FAN_IN;
    }

    public float getFAN_OUT() {
        return FAN_OUT;
    }

    public void setFAN_OUT(float FAN_OUT) {
        this.FAN_OUT = FAN_OUT;
    }

    public float getNORM() {
        return NORM;
    }

    public void setNORM(float NORM) {
        this.NORM = NORM;
    }

    public float getNSF() {
        return NSF;
    }

    public void setNSF(float NSF) {
        this.NSF = NSF;
    }

    public float getNOM() {
        return NOM;
    }

    public void setNOM(float NOM) {
        this.NOM = NOM;
    }

    public float getNOAM() {
        return NOAM;
    }

    public void setNOAM(float NOAM) {
        this.NOAM = NOAM;
    }

    public float getNOPC() {
        return NOPC;
    }

    public void setNOPC(float NOPC) {
        this.NOPC = NOPC;
    }

    public float getNOOF() {
        return NOOF;
    }

    public void setNOOF(float NOOF) {
        this.NOOF = NOOF;
    }

    public float getNCOF() {
        return NCOF;
    }

    public void setNCOF(float NCOF) {
        this.NCOF = NCOF;
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

    public float getNOF() {
        return NOF;
    }

    public void setNOF(float NOF) {
        this.NOF = NOF;
    }



    public static class Builder {
        private final String role;
        private final String roleKind;
        private final String entity;
        private final String designPattern;
        private final String microArchitecture;
        private final String project;
        private String meanVector;
        private float componentMean;

        private final Map<String, Float> metricResults;

        public Builder(RoleEntry roleEntry, String microArchitecture, String designPattern, String project) {
            role = roleEntry.role();
            roleKind = roleEntry.roleKind();
            entity = roleEntry.entity();

            this.designPattern = designPattern;
            this.microArchitecture = microArchitecture;
            this.project = project;
            this.metricResults = new HashMap<>();
        }

        public void setEmbedding(CodeEmbeddingGenerator.Result embedding) {
            var values = Arrays.stream(embedding.meanVector().toDoubleVector())
                    .mapToObj(Double::toString)
                    .collect(Collectors.joining(","));
            this.meanVector = "[" + values + "]";
            this.componentMean = embedding.meanOfAllComponents();
        }

        public void addMetric(String metricName, float value) {
            this.metricResults.put(metricName, value);
        }

        public MetricEvaluationResult toMetricEvaluationResult(boolean includeCKMetrics) {
            var result = new MetricEvaluationResult();
            result.setRole(this.role);
            result.setRoleKind(this.roleKind);
            result.setEntity(this.entity);
            result.setMicroArchitecture(this.microArchitecture);
            result.setDesignPattern(this.designPattern);
            result.setProject(this.project);

            result.setNOF(metricResults.getOrDefault(MetricEvaluationResultConstants.NOF, 0f));
            result.setNSF(metricResults.getOrDefault(MetricEvaluationResultConstants.NSF, 0f));
            result.setNOM(metricResults.getOrDefault(MetricEvaluationResultConstants.NOM, 0f));
            result.setNSM(metricResults.getOrDefault(MetricEvaluationResultConstants.NSM, 0f));
            result.setNOAM(metricResults.getOrDefault(MetricEvaluationResultConstants.NOAM, 0f));
            result.setNORM(metricResults.getOrDefault(MetricEvaluationResultConstants.NORM, 0f));
            result.setNOPC(metricResults.getOrDefault(MetricEvaluationResultConstants.NOPC, 0f));
            result.setNOOF(metricResults.getOrDefault( MetricEvaluationResultConstants.NOOF, 0f));
            result.setNCOF(metricResults.getOrDefault(MetricEvaluationResultConstants.NCOF, 0f));

            if (!includeCKMetrics)
                return result;
            result.setCBO(metricResults.getOrDefault(MetricEvaluationResultConstants.CBO, 0f));
            result.setFAN_IN(metricResults.getOrDefault(MetricEvaluationResultConstants.FAN_IN, 0f));
            result.setFAN_OUT(metricResults.getOrDefault(MetricEvaluationResultConstants.FAN_OUT, 0f));
            result.setNOC(metricResults.getOrDefault(MetricEvaluationResultConstants.NOC, 0f));
            result.setRFC(metricResults.getOrDefault(MetricEvaluationResultConstants.RFC, 0f));
            result.setTCC(metricResults.getOrDefault(MetricEvaluationResultConstants.TCC, 0f));
            result.setLCC(metricResults.getOrDefault(MetricEvaluationResultConstants.LCC, 0f));


            result.setMeanVector(this.meanVector);
            result.setComponentMean(this.componentMean);
            return result;
        }
    }
}
