package com.tilicho.grocery.mangement.utils

data class PurchaseHistoryModel( // Need clarity on complete model
    val id: String = "",
    val itemId: String = "",
    val listId: String = "",
    val quantity: Double = 0.0,
    val price: String = "",  // Need clarity : price is double or string
    val packageSize: Double = 0.0,
    val avaiableQuantity: String = "",
    val createdAt: Long = 0,
    val updatedAt: Long = 0,

    )
