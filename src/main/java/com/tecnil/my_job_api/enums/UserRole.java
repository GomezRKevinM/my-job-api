package com.tecnil.my_job_api.enums;

public enum UserRole {
    admin,
    current;

    public static UserRole fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El rol no puede ser nulo o vacío");
        }
        for (UserRole role : values()) {
            if (role.name().equalsIgnoreCase(value.trim())) {
                return role;
            }
        }
        throw new IllegalArgumentException(String.format("'%s' no es un rol válido. Roles permitidos: admin, current", value));
    }

    public static boolean isValid(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        for (UserRole role : values()) {
            if (role.name().equalsIgnoreCase(value.trim())) {
                return true;
            }
        }
        return false;
    }
}
