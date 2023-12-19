package evaluation;

import com.opencsv.bean.CsvBindByName;

import java.util.Objects;

public final class RoleEntry {
    @CsvBindByName
    private String role;
    @CsvBindByName(column = "role_kind")
    private String roleKind;
    @CsvBindByName
    private String entity;

    public RoleEntry(String role, String roleKind, String entity) {
        this.role = role;
        this.roleKind = roleKind;
        this.entity = entity;
    }

    public RoleEntry() {

    }

    public String role() {
        return role;
    }

    public String roleKind() {
        return roleKind;
    }

    public String entity() {
        return entity;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (RoleEntry) obj;
        return Objects.equals(this.role, that.role) &&
                Objects.equals(this.roleKind, that.roleKind) &&
                Objects.equals(this.entity, that.entity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role, roleKind, entity);
    }

    @Override
    public String toString() {
        return "RoleEntry[" +
                "role=" + role + ", " +
                "roleKind=" + roleKind + ", " +
                "entity=" + entity + ']';
    }

}
