package com.tilicho.grocery.mangement.utils

import com.google.gson.annotations.SerializedName


data class ListsModel(

    @SerializedName("id")
    val id: String = "",

    @SerializedName("name")
    val name: String = "",

    @SerializedName("description")
    val description: String = "",

    @SerializedName("userId")
    val userId: String = "",

    @SerializedName("createdAt")
    val createdAt: Long = 0,

    @SerializedName("updatedAt")
    val updatedAt: Long = 0,

    @SerializedName("imageURL")
    val imageURL: String = "",

    @SerializedName("isRecurring")
    val isRecurring: Boolean = false,

    @SerializedName("recurringDate")
    val recurringDate: Long = 0,

    @SerializedName("istrackingPrice")
    val istrackingPrice: Boolean = false
)