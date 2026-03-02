package com.delivery.project.user.entity;

public enum UserRole {
    CUSTOMER, OWNER, MANAGER, MASTER;

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
