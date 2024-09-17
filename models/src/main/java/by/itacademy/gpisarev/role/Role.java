package by.itacademy.gpisarev.role;

public enum Role {
    ADMIN("Админ"),
    TEACHER("Учитель"),
    STUDENT("Студент");

    private final String roleString;

    Role(String roleString) {
        this.roleString = roleString;
    }

    public String getRoleString() {
        return roleString;
    }
}
