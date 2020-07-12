package com.example.fooderyadmin.data.model

data class Item (
    var id:Int? = null,
    val name:String,
    val category:String,
    val normalPrice:Int,
    val offerPrice:Int,
    val description:String
)
