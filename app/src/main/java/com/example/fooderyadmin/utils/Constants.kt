package com.example.fooderyadmin.utils

import com.example.fooderyadmin.data.model.Admin

class Constants {
    companion object {
        const val SERVER_REF = "Server"
        var currentAdmin: Admin?= null

        // cloud Firestore Collections
        var ITEM_PATH = "Item"
        val CATEGORY_PATH = "Category"
        val ORDER_PATH = "Order"
        val FAVOUTRITE_PATH = "Favourite"    }
}