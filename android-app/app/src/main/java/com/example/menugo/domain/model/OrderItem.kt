package com.example.menugo.domain.model

data class OrderItem(
    val productId: String,
    val productName: String,
    val unitPriceInCents: Long,
    val quantity: Int
) {
    init {
        require(unitPriceInCents >= 0) {
            "Unit price must not be negative."
        }

        require(quantity > 0) {
            "Quantity must be greater than zero."
        }
    }

    val subtotalInCents: Long
        get() = unitPriceInCents * quantity
}