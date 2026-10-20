package com.example.menugo.domain.model

data class Order(
    val id: String,
    val items: List<OrderItem>,
    val branch: Branch,
    val createdAtEpochMillis: Long,
    val status: OrderStatus = OrderStatus.PENDING
) {
    init {
        require(items.isNotEmpty()) {
            "An order must contain at least one item."
        }
    }

    val totalInCents: Long
        get() = items.sumOf { it.subtotalInCents }
}