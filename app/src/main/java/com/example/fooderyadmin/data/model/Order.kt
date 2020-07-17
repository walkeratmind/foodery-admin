package com.example.fooderyadmin.data.model

data class Order(
    val id:String,
    val itemId: String,
    val userId: String,
    val orderDate: String,
    val status: String  // orderStatus = PROCESS, DElIVERED, CANCELED
)