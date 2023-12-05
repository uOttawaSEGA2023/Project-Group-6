package com.example.seg2105_project;

import org.testng.annotations.Test;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

public class MainActivityTest {

    @Test
    public void testValidEmail() {
        assertTrue(MainActivity.validEmail("test@example.com"));
        assertFalse(MainActivity.validEmail("invalid-email"));
    }

    @Test
    public void testValidPassword() {
        assertTrue(MainActivity.validPassword("Password123"));
        assertFalse(MainActivity.validPassword("short"));
        assertFalse(MainActivity.validPassword("nouppercase123"));
        assertFalse(MainActivity.validPassword("NOLOWER123"));
        assertFalse(MainActivity.validPassword("noDigitUpperCase"));
    }
}
