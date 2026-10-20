package com.example.menugo.domain.model

data class Branch(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val openingHours: String,
    val isActive: Boolean = true
)