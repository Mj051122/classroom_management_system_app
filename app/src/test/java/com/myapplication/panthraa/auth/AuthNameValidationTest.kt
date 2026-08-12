package com.myapplication.panthraa.auth

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthNameValidationTest {
    @Test
    fun acceptsNamesWithoutNumbers() {
        assertFalse(AuthViewModel.containsNumber("Juan"))
        assertFalse(AuthViewModel.containsNumber("Dela Cruz"))
        assertFalse(AuthViewModel.containsNumber("Mark James"))
        assertFalse(AuthViewModel.containsNumber("Nguyễn"))
        assertFalse(AuthViewModel.containsNumber("José Rizal"))
        assertFalse(AuthViewModel.containsNumber(""))
        assertFalse(AuthViewModel.containsNumber("   "))
    }

    @Test
    fun rejectsNamesContainingDigits() {
        assertTrue(AuthViewModel.containsNumber("Mark1"))
        assertTrue(AuthViewModel.containsNumber("1"))
        assertTrue(AuthViewModel.containsNumber("J0hn"))
        assertTrue(AuthViewModel.containsNumber("Juan 2"))
        assertTrue(AuthViewModel.containsNumber(" 3 "))
    }
}