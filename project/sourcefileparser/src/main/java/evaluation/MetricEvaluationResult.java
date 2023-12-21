package evaluation;

import com.opencsv.bean.CsvBindByName;

import java.util.HashMap;
import java.util.Map;

public class MetricEvaluationResult {
    @CsvBindByName(column = "role")
    public String role;
    @CsvBindByName(column = "role_kind")
    private String roleKind;
    @CsvBindByName(column = "entity")
    private String entity;
    @CsvBindByName(column = "design_pattern")
    private String designPattern;
    @CsvBindByName(column = "micro_architecture")
    private String microArchitecture;

    @CsvBindByName(column = "project")
    private String project;

    //Number of Fields
    @CsvBindByName(column = "NOF")
    private float NOF;

    //Number of static Fields
    @CsvBindByName(column = "NSF")
    private float NSF;
    //Number of Methods
    @CsvBindByName(column = "NOM")
    private float NOM;

    //Number of Static Methods
    @CsvBindByName(column = "NSM")
    private float NSM;

    //Number of Abstract Methods
    @CsvBindByName(column = "NOAM")
    private float NOAM;

    //Number of Private Constructors
    @CsvBindByName(column = "NOPC")
    private float NOPC;

    //Number of Overridden Methods
    @CsvBindByName(column = "NORM")
    private float NORM;

    //Number of Object Fields
    @CsvBindByName(column = "NOOF")
    private float NOOF;

    //Number of Other Classes With Field Of Own Type
    //TODO: Same as FAN_OUT?
    @CsvBindByName(column = "NCOF")
    private float NCOF;

    //Coupling between objects
    @CsvBindByName(column = "CBO")
    private float CBO;

    //Number of Input Dependencies
    @CsvBindByName(column = "FAN_IN")
    private float FAN_IN;

    //Number of Output Dependencies
    @CsvBindByName(column = "FAN_OUT")
    private float FAN_OUT;

    public MetricEvaluationResult() {
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

            result.setNOF(metricResults.getOrDefault("NOF", 0f));
            result.setNSF(metricResults.getOrDefault("NSF", 0f));
            result.setNOM(metricResults.getOrDefault("NOM", 0f));
            result.setNSM(metricResults.getOrDefault("NSM", 0f));
            result.setNOAM(metricResults.getOrDefault("NOAM", 0f));
            result.setNORM(metricResults.getOrDefault("NORM", 0f));
            result.setNOPC(metricResults.getOrDefault("NOPC", 0f));
            result.setNOOF(metricResults.getOrDefault("NOOF", 0f));
            result.setNCOF(metricResults.getOrDefault("NCOF", 0f));

            if (!includeCKMetrics)
                return result;
            result.setCBO(metricResults.getOrDefault("CBO", 0f));
            result.setFAN_IN(metricResults.getOrDefault("FAN_IN", 0f));
            result.setFAN_OUT(metricResults.getOrDefault("FAN_OUT", 0f));
            return result;
        }
    }
}