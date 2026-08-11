package com.myapplication.panthraa.auth

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthPhoneValidationTest {
    @Test
    fun acceptsPhilippineMobileNumbers() {
        assertTrue(AuthViewModel.isValidPhilippinePhone("09171234567"))
        assertTrue(AuthViewModel.isValidPhilippinePhone("+639171234567"))
    }

    @Test
    fun rejectsNumbersOutsidePhilippineFormat() {
        assertFalse(AuthViewModel.isValidPhilippinePhone("0917123456"))
        assertFalse(AuthViewModel.isValidPhilippinePhone("091712345678"))
        assertFalse(AuthViewModel.isValidPhilippinePhone("+63917123456"))
        assertFalse(AuthViewModel.isValidPhilippinePhone("639171234567"))
        assertFalse(AuthViewModel.isValidPhilippinePhone("12345678901"))
        assertFalse(AuthViewModel.isValidPhilippinePhone(""))
    }

    @Test
    fun rejectsBlankAndNonDigitInput() {
        assertFalse(AuthViewModel.isValidPhilippinePhone("0917 123 4567"))
        assertFalse(AuthViewModel.isValidPhilippinePhone("0917-123-4567"))
        assertFalse(AuthViewModel.isValidPhilippinePhone("phone number"))
    }
}