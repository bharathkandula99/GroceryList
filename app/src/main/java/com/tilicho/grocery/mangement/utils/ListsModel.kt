package com.tilicho.grocery.mangement.utils

data class ListsModel(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val userId: String = "",
    val createdAt: Long = 0,
    val UpdatedAt: Long = 0,
    val imageURL: String = "",
    val isRecurring: Boolean = false,
    val recurringDate: Long = 0,
    val istractingPrice: Boolean = false
)