package com.tilicho.grocery.mangement.utils

data class FoodWastageModel(
    val id: String = "",
    val userId: String = "",
    val wastage: Double = 0.0,
    val createdAt: Long = 0,
    val mealType: String = ""
)
