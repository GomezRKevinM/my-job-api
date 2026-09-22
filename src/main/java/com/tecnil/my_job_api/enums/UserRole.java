package com.tecnil.my_job_api.enums;

public enum UserRole {
    admin,
    current;

    public static  UserRole fromString(String value) {
        for(UserRole role : values()) {
            if(role.name().equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw  new IllegalArgumentException(String.format("%s is not a valid role", value));
    }
}
