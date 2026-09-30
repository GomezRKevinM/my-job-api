package com.tecnil.my_job_api.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NivelEmpleoTest {

    @Test
    void fromString_ValidValues_ShouldReturnEnum() {
        assertEquals(NivelEmpleo.JUNIOR, NivelEmpleo.fromString("junior"));
        assertEquals(NivelEmpleo.MID, NivelEmpleo.fromString("mid"));
        assertEquals(NivelEmpleo.SENIOR, NivelEmpleo.fromString("SENIOR"));
        assertEquals(NivelEmpleo.TRAINEE, NivelEmpleo.fromString("trainee"));
        assertEquals(NivelEmpleo.LEAD, NivelEmpleo.fromString("lead"));
        assertEquals(NivelEmpleo.DIRECTOR, NivelEmpleo.fromString("director"));
    }

    @Test
    void fromString_InvalidValue_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> NivelEmpleo.fromString("INVENTADO"));
        assertThrows(IllegalArgumentException.class, () -> NivelEmpleo.fromString(null));
        assertThrows(IllegalArgumentException.class, () -> NivelEmpleo.fromString("   "));
    }

    @Test
    void isValid_ShouldReturnTrueForValidAndFalseForInvalid() {
        assertTrue(NivelEmpleo.isValid("junior"));
        assertTrue(NivelEmpleo.isValid("SENIOR"));
        assertFalse(NivelEmpleo.isValid("unknown"));
        assertFalse(NivelEmpleo.isValid(null));
        assertFalse(NivelEmpleo.isValid(""));
    }
}
