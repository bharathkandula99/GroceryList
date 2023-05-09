package com.tilicho.grocery.mangement.utils

data class ItemConsumptions(
    val id: String = "",
    val itemId: String = "",
    val quantity: Double = 0.0,
    val availableQuantity: Double = 0.0,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val packageSize: Double = 0.0,
    val userId: String = ""
)
