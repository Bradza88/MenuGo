package com.example.menugo.domain.model

data class CartItem(
    val product: Product,
    val quantity: Int
) {
    init {
        require(quantity > 0) {
            "Quantity must be greater than zero."
        }
    }

    val subtotalInCents: Long
        get() = product.priceInCents * quantity
}