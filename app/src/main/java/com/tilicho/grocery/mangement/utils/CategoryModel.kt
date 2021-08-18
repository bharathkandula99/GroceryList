package com.tilicho.grocery.mangement.utils

data class CategoryModel(val id: String = "",
                         val name: String = "",
                         val imageUrl: String = "",
                         val createdAt: Long = 0,
                         val categoryForUserId: String = "",
                         val updatedAt:Long = 0
)
