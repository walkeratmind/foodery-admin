package com.example.fooderyadmin.ui.main.view.ui.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooderyadmin.data.model.Item
import com.example.fooderyadmin.data.model.ItemResponse
import com.example.fooderyadmin.utils.Constants
import com.example.fooderyadmin.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    val foodItems: MutableLiveData<Resource<MutableList<Item>>> = MutableLiveData()

    init {

        getFoodItems()

    }

    fun getFoodItems() = viewModelScope.launch {
        foodItems.postValue(Resource.Loading())

        // Read data from Cloud Firestore
        db.collection(Constants.ITEM_REF).get()
            .addOnSuccessListener { result ->
                val itemList: MutableList<Item>? = mutableListOf()
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val item: Item = Item(
                        document.id,
                        document.get("itemName").toString(),
                        document["itemType"].toString(),
                        document["category"].toString(),
                        document["normalPrice"].toString().toInt(),
                        document["offerPrice"].toString().toInt(),
                        document["description"].toString()
                    )
                    Log.d(TAG, "Item: ${item}")
                    itemList?.add(item)
                }
                foodItems.postValue(Resource.Success(itemList!!))
            }
            .addOnFailureListener { exception ->
                foodItems.postValue(Resource.Error(exception.toString()))
                Log.w(TAG, "Error getting documents.", exception)
            }


    }

    companion object {
        const val TAG = "HomeViewModel"
    }

}