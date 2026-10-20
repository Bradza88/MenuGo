package com.example.menugo.domain.model

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val priceInCents: Long,
    val category: ProductCategory,
    val imageUri: String? = null,
    val isOnPromotion: Boolean = false,
    val isAvailable: Boolean = true
)