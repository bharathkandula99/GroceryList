package com.tilicho.grocery.mangement.utils

data class ListItemModel(
    val id: String = "",
    val itemId: String = "",
    val listId: String = "",
    val userId: String = "",
    val quantity: Double = 0.0,
    val packageSize: Double = 0.0,
    val purchased: Boolean = false,
    val purchasedDate: Long = 0,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val deleted: Boolean = false,
    val unitId: String = "",
    val categoryId: String = "",
    val itemName: String = ""
)
