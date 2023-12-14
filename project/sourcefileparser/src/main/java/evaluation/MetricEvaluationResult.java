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

    @CsvBindByName(column = "NOF")
    private int NOF;
    @CsvBindByName(column = "NSF")
    private int NSF;
    @CsvBindByName(column = "NOM")
    private int NOM;
    @CsvBindByName(column = "NOI")
    private int NOI;

    public int getNSM() {
        return NSM;
    }

    public void setNSM(int NSM) {
        this.NSM = NSM;
    }

    @CsvBindByName(column = "NSM")
    private int NSM;

    @CsvBindByName(column = "NOAM")
    private int NOAM;
    @CsvBindByName(column = "NOPC")
    private int NOPC;

    @CsvBindByName(column = "NORM")
    private int NORM;

    @CsvBindByName(column = "NOTC")
    private int NOTC;

    @CsvBindByName(column = "NOOF")
    private int NOOF;

    @CsvBindByName(column = "NCOP")
    private int NCOF;

    public int getNORM() {
        return NORM;
    }

    public void setNORM(int NORM) {
        this.NORM = NORM;
    }

    public int getNSF() {
        return NSF;
    }

    public void setNSF(int NSF) {
        this.NSF = NSF;
    }

    public int getNOM() {
        return NOM;
    }

    public void setNOM(int NOM) {
        this.NOM = NOM;
    }

    public int getNOI() {
        return NOI;
    }

    public void setNOI(int NOI) {
        this.NOI = NOI;
    }

    public int getNOAM() {
        return NOAM;
    }

    public void setNOAM(int NOAM) {
        this.NOAM = NOAM;
    }

    public int getNOPC() {
        return NOPC;
    }

    public void setNOPC(int NOPC) {
        this.NOPC = NOPC;
    }

    public int getNOTC() {
        return NOTC;
    }

    public void setNOTC(int NOTC) {
        this.NOTC = NOTC;
    }

    public int getNOOF() {
        return NOOF;
    }

    public void setNOOF(int NOOF) {
        this.NOOF = NOOF;
    }

    public int getNCOF() {
        return NCOF;
    }

    public void setNCOF(int NCOF) {
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

    public int getNOF() {
        return NOF;
    }

    public void setNOF(int NOF) {
        this.NOF = NOF;
    }

    public MetricEvaluationResult() {
    }

    public static class ParsedEntityInformation {
        private final String role;
        private final String roleKind;
        private final String entity;
        private final String designPattern;
        private final String microArchitecture;
        private final String project;

        private final Map<String, Integer> metricResults;

        public ParsedEntityInformation(RoleEntry roleEntry, String microArchitecture, String designPattern, String project) {
            role = roleEntry.role();
            roleKind = roleEntry.roleKind();
            entity = roleEntry.entity();

            this.designPattern = designPattern;
            this.microArchitecture = microArchitecture;
            this.project = project;
            this.metricResults = new HashMap<>();
        }

        public void addMetric(String metricName, int value) {
            this.metricResults.put(metricName, value);
        }

        public MetricEvaluationResult toMetricEvaluationResult() {
            var result = new MetricEvaluationResult();
            result.setRole(this.role);
            result.setRoleKind(this.roleKind);
            result.setEntity(this.entity);
            result.setMicroArchitecture(this.microArchitecture);
            result.setDesignPattern(this.designPattern);
            result.setProject(this.project);

            result.setNOF(metricResults.getOrDefault("NOF", 0));
            result.setNSF(metricResults.getOrDefault("NSF", 0));
            result.setNOM(metricResults.getOrDefault("NOM", 0));
            result.setNSM(metricResults.getOrDefault("NSM", 0));
            result.setNOI(metricResults.getOrDefault("NOI", 0));
            result.setNOAM(metricResults.getOrDefault("NOAM", 0));
            result.setNORM(metricResults.getOrDefault("NORM", 0));
            result.setNOPC(metricResults.getOrDefault("NOPC", 0));
            result.setNOTC(metricResults.getOrDefault("NOTC", 0));
            result.setNOOF(metricResults.getOrDefault("NOOF", 0));
            result.setNCOF(metricResults.getOrDefault("NOF", 0));
            return result;
        }
    }
}
