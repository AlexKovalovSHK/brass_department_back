package com.brass_admin_d.brass_admin_d.department.model;

public enum DepartmentStatus {
    CLOSED("закрыт"),
    OPEN("открыт");


    private final String description;

    DepartmentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
