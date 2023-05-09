package com.tilicho.grocery.mangement.utils

data class UserModel(
    val id: String = "",
    val firstName: String = "",
    val lastName: String ="",
    val email: String ="",
    val createdAt: Long = 0,
    val profileImageURL: String = ""
)
