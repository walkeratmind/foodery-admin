package com.example.fooderyadmin.utils

import com.example.fooderyadmin.data.model.Admin

class Constants {
    companion object {
        const val SERVER_REF = "Server"
        var currentAdmin: Admin? = null

        const val USER_REF: String = "Users"

        // Item in Cart
        const val MAX_ITEM = 20
        const val MIN_ITEM = 1

        // cloud Firestore Collections
        const val ITEM_REF = "Item"
        const val CATEGORY_REF = "Categories"
        const val ORDER_REF = "Orders"
        const val CART_REF = "Carts"
        const val FAVOURITE_REF = "Favourites"
    }
}