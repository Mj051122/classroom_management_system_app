package com.myapplication.panthraa.auth

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthEmailValidationTest {
    @Test
    fun acceptsStandardEmailAddresses() {
        assertTrue(AuthViewModel.isValidEmail("student@example.com"))
        assertTrue(AuthViewModel.isValidEmail("mark.james+presentation@school.edu.ph"))
    }

    @Test
    fun rejectsBlankAndMalformedEmailAddresses() {
        assertFalse(AuthViewModel.isValidEmail(""))
        assertFalse(AuthViewModel.isValidEmail("student@"))
        assertFalse(AuthViewModel.isValidEmail("@school.edu"))
        assertFalse(AuthViewModel.isValidEmail("not-an-email"))
    }
}
