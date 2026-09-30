package com.tecnil.my_job_api.enums;

public enum NivelEmpleo {
    TRAINEE,
    JUNIOR,
    MID,
    SENIOR,
    LEAD,
    DIRECTOR;

    public static NivelEmpleo fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El nivel no puede ser nulo o vacío");
        }
        for (NivelEmpleo nivel : values()) {
            if (nivel.name().equalsIgnoreCase(value.trim())) {
                return nivel;
            }
        }
        throw new IllegalArgumentException(String.format("'%s' no es un nivel válido. Niveles permitidos: TRAINEE, JUNIOR, MID, SENIOR, LEAD, DIRECTOR", value));
    }

    public static boolean isValid(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        for (NivelEmpleo nivel : values()) {
            if (nivel.name().equalsIgnoreCase(value.trim())) {
                return true;
            }
        }
        return false;
    }
}
