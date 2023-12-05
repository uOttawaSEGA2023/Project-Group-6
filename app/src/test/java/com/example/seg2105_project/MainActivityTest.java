package com.example.seg2105_project;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MainActivityTest {

    @Test
    public void testValidEmail() {
        assertTrue(MainActivity.validEmail("test@example.com"));
    }

    public void testInvalidEmail() {
        assertFalse(MainActivity.validEmail("invalid-email"));
    }

    @Test
    public void testValidPassword() {
        assertTrue(MainActivity.validPassword("Password123"));
    }

    @Test
    public void testInvalidShortPassword() {
        assertFalse(MainActivity.validPassword("short"));
        assertFalse(MainActivity.validPassword("noDigitUpperCase"));
    }

}
