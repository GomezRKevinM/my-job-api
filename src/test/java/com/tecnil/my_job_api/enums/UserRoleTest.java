package com.tecnil.my_job_api.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRoleTest {

    @Test
    void fromString_WithValidRoles_ShouldReturnEnum() {
        assertEquals(UserRole.admin, UserRole.fromString("admin"));
        assertEquals(UserRole.admin, UserRole.fromString("ADMIN"));
        assertEquals(UserRole.current, UserRole.fromString("current"));
        assertEquals(UserRole.current, UserRole.fromString("CURRENT"));
    }

    @Test
    void fromString_WithInvalidRole_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> UserRole.fromString("invalid_role"));
    }

    @Test
    void fromString_WithNullOrEmpty_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> UserRole.fromString(null));
        assertThrows(IllegalArgumentException.class, () -> UserRole.fromString("   "));
    }

    @Test
    void isValid_ShouldReturnExpectedBoolean() {
        assertTrue(UserRole.isValid("admin"));
        assertTrue(UserRole.isValid("ADMIN"));
        assertTrue(UserRole.isValid("current"));
        assertFalse(UserRole.isValid("superadmin"));
        assertFalse(UserRole.isValid(null));
        assertFalse(UserRole.isValid(""));
    }
}
