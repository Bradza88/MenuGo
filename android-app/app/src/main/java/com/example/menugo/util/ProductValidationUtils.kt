
package com.example.menugo.util

import java.math.BigDecimal
import java.math.RoundingMode

object ProductValidationUtils {

    fun validateRequiredText(value: String): Boolean {
        return value.isNotBlank()
    }

    fun validatePrice(price: BigDecimal): Boolean {
        return price >= BigDecimal.ZERO
    }

    fun priceToCents(priceText: String): Long? {
        val normalizedPrice = priceText.trim().replace(',', '.')

        if (normalizedPrice.isBlank()) {
            return null
        }

        return try {
            val price = BigDecimal(normalizedPrice)

            if (!validatePrice(price)) {
                return null
            }

            price.multiply(BigDecimal(100))
                .setScale(0, RoundingMode.UNNECESSARY)
                .longValueExact()
        } catch (_: NumberFormatException) {
            null
        } catch (_: ArithmeticException) {
            null
        }
    }

    fun validatePriceRange(
        minPriceInCents: Long?,
        maxPriceInCents: Long?
    ): Boolean {
        if (minPriceInCents != null && minPriceInCents < 0L) {
            return false
        }

        if (maxPriceInCents != null && maxPriceInCents < 0L) {
            return false
        }

        return minPriceInCents == null ||
                maxPriceInCents == null ||
                minPriceInCents <= maxPriceInCents
    }
}
