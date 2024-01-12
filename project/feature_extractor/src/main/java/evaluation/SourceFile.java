package evaluation;

import com.opencsv.bean.CsvBindByName;

public final class SourceFile {
    @CsvBindByName(column = ClassMetricVectorConstants.ROLE)
    private String role;
    @CsvBindByName(column = ClassMetricVectorConstants.ROLE_KIND)
    private String roleKind;
    @CsvBindByName(column = ClassMetricVectorConstants.ENTITY)
    private String entity;

    @CsvBindByName(column = ClassMetricVectorConstants.PROJECT)
    private String project;

    @CsvBindByName(column = ClassMetricVectorConstants.MICRO_ARCHITECTURE)
    private String microArchitecture;

    @CsvBindByName(column = ClassMetricVectorConstants.DESIGN_PATTERN)
    private String designPattern;

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

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getMicroArchitecture() {
        return microArchitecture;
    }

    public void setMicroArchitecture(String microArchitecture) {
        this.microArchitecture = microArchitecture;
    }

    public String getDesignPattern() {
        return designPattern;
    }

    public void setDesignPattern(String designPattern) {
        this.designPattern = designPattern;
    }
}
