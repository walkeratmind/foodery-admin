package com.example.fooderyadmin.ui.main.view.ui.item

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fooderyadmin.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore

class ItemViewModel : ViewModel() {
    val itemName = MutableLiveData<String>("")

    val itemType = MutableLiveData<String>("Non Veg.")

    val itemCategory = MutableLiveData<String>("")

    val normalPrice = MutableLiveData<String>()

    val offerPrice = MutableLiveData<String>()
    val description = MutableLiveData<String>()


    val isAdded = MutableLiveData<Boolean>()


    init {
        Log.d(TAG, "On ItemViewModel Created")
        isAdded.value = false
    }

    fun isFormValid():Boolean {
        return true
    }

    fun addItem() {
        val db = FirebaseFirestore.getInstance()

        val item = hashMapOf(
            "itemName" to itemName.value,
            "itemType" to "Non Veg.",
            "category" to itemCategory.value,
            "normalPrice" to normalPrice.value,
            "offerPrice" to offerPrice.value,
            "description" to description.value
        )
        db.collection(Constants.ITEM_PATH)
            .add(item)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                isAdded.value = true
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error adding document", exception)
            }



    }

    companion object {
        const val TAG = "ItemViewModel"
    }


}