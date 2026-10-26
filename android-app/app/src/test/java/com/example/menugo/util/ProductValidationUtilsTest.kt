package com.example.menugo.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductValidationUtilsTest {

    @Test
    fun convertsPricesWithEitherDecimalSeparator() {
        assertEquals(
            1250L,
            requireNotNull(ProductValidationUtils.priceToCents("12.50"))
        )
        assertEquals(
            1250L,
            requireNotNull(ProductValidationUtils.priceToCents(" 12,50 "))
        )
        assertEquals(
            300000L,
            requireNotNull(ProductValidationUtils.priceToCents("3000"))
        )
        assertEquals(
            0L,
            requireNotNull(ProductValidationUtils.priceToCents("0"))
        )
    }

    @Test
    fun rejectsInvalidNegativeAndUnrepresentablePrices() {
        val invalidPrices = listOf(
            "",
            "   ",
            "abc",
            "-1",
            "12.345",
            "1,234.56",
            "92233720368547758.08"
        )

        invalidPrices.forEach { input ->
            assertNull(
                "Expected an invalid price: $input",
                ProductValidationUtils.priceToCents(input)
            )
        }
    }

    @Test
    fun rejectsBlankRequiredText() {
        assertFalse(ProductValidationUtils.validateRequiredText(""))
        assertFalse(ProductValidationUtils.validateRequiredText("   "))
        assertTrue(
            ProductValidationUtils.validateRequiredText("Classic Burger")
        )
    }

    @Test
    fun validatesOptionalPriceLimits() {
        assertTrue(ProductValidationUtils.validatePriceRange(null, null))
        assertTrue(ProductValidationUtils.validatePriceRange(100L, null))
        assertTrue(ProductValidationUtils.validatePriceRange(null, 500L))
        assertTrue(ProductValidationUtils.validatePriceRange(100L, 500L))

        assertFalse(ProductValidationUtils.validatePriceRange(-1L, null))
        assertFalse(ProductValidationUtils.validatePriceRange(null, -1L))
        assertFalse(ProductValidationUtils.validatePriceRange(500L, 100L))
    }
}