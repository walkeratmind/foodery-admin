package com.example.fooderyadmin.data.model

data class ItemResponse(
    val items: List<Item>,
    val status: String,
    val totalResults: Int
)