package com.example.menugo.util

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object MoneyFormatter {

    fun format(
        amountInCents: Long,
        locale: Locale = Locale.getDefault()
    ): String {
        val amount = BigDecimal.valueOf(amountInCents, 2)

        val formatter = NumberFormat.getCurrencyInstance(locale).apply {
            currency = Currency.getInstance("CRC")
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }

        return formatter.format(amount)
    }
}