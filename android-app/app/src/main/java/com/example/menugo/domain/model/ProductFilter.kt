package com.example.menugo.domain.model

data class ProductFilter(
    val query: String = "",
    val category: ProductCategory? = null,
    val minPriceInCents: Long? = null,
    val maxPriceInCents: Long? = null,
    val onlyPromotions: Boolean = false
)