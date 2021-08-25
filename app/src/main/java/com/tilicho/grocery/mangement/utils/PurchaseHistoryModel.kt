package com.tilicho.grocery.mangement.utils

data class PurchaseHistoryModel(
    val id: String = "",
    val itemId: String = "",
    val listId: String = "",
    val quantity: Double = 0.0,
    var price: String = "",
    val packageSize: Double = 0.0,
    val availableQuantity: Double = 0.0,
    val updatedAt: Long = 0,
    val createdAt: Long = 0,
    val userId: String = "",
    val itemName: String = "",
    val unitId: String = "",
    val categoryId: String = ""
)
