package com.tilicho.grocery.mangement.utils

data class ListItem(
    val id: String = "",
    val groceryItemId: String = "",
    val listId: String = "",
    val userId: String = "",
    val quantity: Double = 0.0,
    val packageSize: Double = 0.0,
    val isPurchased: Boolean = false,
    val purchasedDate: Long = 0,
    val createdAt: Long = 0,
    val UpdatedAt: Long = 0,
    val isDeleted: Boolean = false,
    val unitId: String = "",
    val categoryId: String = "",
    val itemName: Boolean = false
)
