package com.brass_admin_d.brass_admin_d.department.model;

import lombok.Getter;

@Getter
public enum DepartmentStatus {
    CLOSED("закрыт"),
    OPEN("открыт");

    private final String description;

    DepartmentStatus(String description) {
        this.description = description;
    }

}
