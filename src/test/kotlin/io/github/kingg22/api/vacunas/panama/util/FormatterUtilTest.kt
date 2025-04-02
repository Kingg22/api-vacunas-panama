package io.github.kingg22.api.vacunas.panama.util

import io.github.kingg22.api.vacunas.panama.util.FormatterUtil.FormatterResult.ResultType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class FormatterUtilTest {

    @Test
    fun `formatCedula should format incorrect pattern cedulas`() {
        // Test with various incorrect formats that should be formatted
        assertEquals("3-0123-012345", FormatterUtil.formatCedula("3-123-12345"))
        assertEquals("8AV-0056-000789", FormatterUtil.formatCedula("8AV-56-789"))
        assertEquals("PE-0001-000001", FormatterUtil.formatCedula("PE-1-1"))
        assertEquals("E-0999-000088", FormatterUtil.formatCedula("E-999-88"))
        assertEquals("N-1234-001234", FormatterUtil.formatCedula("N-1234-1234"))
        assertEquals("13PI-0042-000007", FormatterUtil.formatCedula("13PI-42-7"))
    }

    @Test
    fun `formatCedula should return already correctly formatted cedulas`() {
        // Test with already correctly formatted cedulas
        val correctCedulas = listOf(
            "8-0123-012345",
            "PE-0001-000001",
            "E-1234-123456",
            "N-9999-999999",
            "5PI-0042-000789",
            "10AV-0001-000001",
        )

        for (cedula in correctCedulas) {
            assertEquals(cedula, FormatterUtil.formatCedula(cedula))
        }
    }

    @Test
    fun `formatCedula should throw exception for invalid patterns`() {
        // Test with invalid patterns that should throw exceptions
        val invalidCedulas = listOf(
            "X-1234-123456", // Invalid prefix
            "14-1234-123456", // Invalid prefix (>13)
            "8-12345-123456", // Libro too long
            "8-1234-1234567", // Tomo too long
            "8--1234-123456", // Double dash
            "8-1234--123456", // Double dash
            "abc123", // Completely invalid format
            "", // Empty string
        )

        for (cedula in invalidCedulas) {
            assertFailsWith<IllegalArgumentException> {
                FormatterUtil.formatCedula(cedula)
            }
        }
    }

    @Test
    fun `formatIdTemporal should format RI patterns`() {
        // Test formatting RI patterns
        assertEquals("RN1-8-0123-0456", FormatterUtil.formatIdTemporal("RN1-8-123-456"))
        assertEquals("RN12-PE-0001-0001", FormatterUtil.formatIdTemporal("RN12-PE-1-1"))
        assertEquals("RN-E-0999-0088", FormatterUtil.formatIdTemporal("RN-E-999-88"))
    }

    @Test
    fun `formatIdTemporal should return NI patterns without changes`() {
        // Test NI patterns are returned without changes
        val niPatterns = listOf(
            "NI-12345",
            "NI-ABC123",
            "NI-SOMETHING_ELSE",
        )

        for (pattern in niPatterns) {
            assertEquals(pattern, FormatterUtil.formatIdTemporal(pattern))
        }
    }

    @Test
    fun `formatIdTemporal should throw exception for invalid patterns`() {
        // Test invalid patterns
        val invalidPatterns = listOf(
            "XX-8-1234-123456", // Invalid prefix
            "RN123-8-1234-123456", // RN number too high
            "RN-8-12345-123456", // Libro too long
            "RN-8-1234-1234567", // Tomo too long
            "random string", // Completely invalid
        )

        for (pattern in invalidPatterns) {
            assertFailsWith<IllegalArgumentException> {
                FormatterUtil.formatIdTemporal(pattern)
            }
        }
    }

    @Test
    fun `formatToSearch should identify cedula`() {
        // Test cedula identification
        val result = FormatterUtil.formatToSearch("8-123-456")
        assertEquals("8-0123-000456", result.cedula)
        assertNull(result.pasaporte)
        assertNull(result.correo)
        assertEquals("8-0123-000456", result.getIdentifier())
        assertEquals(ResultType.CEDULA, result.getTypeIdentifier())
    }

    @Test
    fun `formatToSearch should identify pasaporte`() {
        // Test pasaporte identification (5-20 alphanumeric characters)
        val result = FormatterUtil.formatToSearch("AB12345678")
        assertNull(result.cedula)
        assertEquals("AB12345678", result.pasaporte)
        assertNull(result.correo)
        assertEquals("AB12345678", result.getIdentifier())
        assertEquals(ResultType.PASAPORTE, result.getTypeIdentifier())
    }

    @Test
    fun `formatToSearch should identify email`() {
        // Test email identification
        val result = FormatterUtil.formatToSearch("test@example.com")
        assertNull(result.cedula)
        assertNull(result.pasaporte)
        assertEquals("test@example.com", result.correo)
        assertEquals("test@example.com", result.getIdentifier())
        assertEquals(ResultType.CORREO, result.getTypeIdentifier())
    }

    @Test
    fun `formatToSearch should handle complex emails`() {
        // Test complex valid emails
        val emails = listOf(
            "test.name+tag@example.com",
            "very.common@example.com",
            "disposable.style.email.with+symbol@example.com",
            "other.email-with-hyphen@example.com",
            "fully-qualified-domain@example.com",
            "user.name+tag+sorting@example.com",
        )

        for (email in emails) {
            val result = FormatterUtil.formatToSearch(email)
            assertEquals(email, result.correo)
            assertEquals(ResultType.CORREO, result.getTypeIdentifier())
        }
    }

    @Test
    fun `formatToSearch should handle invalid identifiers`() {
        // Test with input that doesn't match any pattern
        assertFailsWith<IllegalArgumentException> {
            val result = FormatterUtil.formatToSearch("not_matching_any_pattern")
            result.getIdentifier() // This should throw because no identifier was found
        }

        assertFailsWith<IllegalArgumentException> {
            val result = FormatterUtil.formatToSearch("not_matching_any_pattern")
            result.getTypeIdentifier() // This should throw because no identifier was found
        }
    }

    @Test
    fun `FormatterResult throw exception if have more than 1 result`() {
        // This test is just in case the input somehow matches multiple patterns
        assertFailsWith<IllegalArgumentException> {
            FormatterUtil.FormatterResult(
                cedula = "8-0123-000456",
                pasaporte = "AB12345",
                correo = "test@example.com",
            )
        }
    }

    @Test
    fun `FormatterResult return correct type with multiple params`() {
        // This test is just in case the input somehow matches multiple patterns
        // We create a mock FormatterResult to test the priority logic
        val result = FormatterUtil.FormatterResult(cedula = "8-0123-000456", pasaporte = "  ", correo = null)

        // According to the implementation, the priority is: cedula, then pasaporte, then correo
        assertEquals("8-0123-000456", result.getIdentifier())
        assertEquals(ResultType.CEDULA, result.getTypeIdentifier())
    }

    @Test
    fun `formatRI private method should be tested through formatIdTemporal`() {
        // Since formatRI is private, we test it through formatIdTemporal
        // Correct RI format
        assertEquals("RN-8-0123-0456", FormatterUtil.formatIdTemporal("RN-8-123-456"))

        // Already correct RI format should remain unchanged
        assertEquals("RN-8-0123-0456", FormatterUtil.formatIdTemporal("RN-8-0123-0456"))

        // Invalid RI format should throw exception
        assertFailsWith<IllegalArgumentException> {
            FormatterUtil.formatIdTemporal("RN-X-123-456")
        }
    }
}
