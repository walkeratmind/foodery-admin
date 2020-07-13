package com.example.fooderyadmin.ui.main.view.ui.item

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class ItemViewModel : ViewModel() {
    private val _itemName = MutableLiveData<String>("")
    val itemName: LiveData<String>
        get() = _itemName
    private val _itemType = MutableLiveData<String>("Non Veg.")
    val itemType: LiveData<String>
        get() = _itemType
    private val _itemCategory = MutableLiveData<String>("")
    val itemCategory: LiveData<String>
        get() = _itemCategory
    private val _normalPrice = MutableLiveData<String>()
    val normalPrice: LiveData<String>
        get() = _normalPrice
    private val _offerPrice = MutableLiveData<String>()
    val offerPrice: LiveData<String>
        get() = _offerPrice
    private val _description = MutableLiveData<String>()
    val description: LiveData<String>
    get() = _description

    private val _isAdded = MutableLiveData<Boolean>()
    val isAdded: LiveData<Boolean>
    get() = _isAdded

    init {
        Log.d(TAG, "On ItemViewModel Created")
        _isAdded.value = false
    }

    fun isFormValid():Boolean {
        return true
    }

    fun addItem() {
        val db = FirebaseFirestore.getInstance()

        val item = hashMapOf(
            "itemName" to _itemName.value,
            "itemType" to "Non Veg.",
            "category" to _itemCategory.value,
            "normalPrice" to _normalPrice.value,
            "offerPrice" to _offerPrice.value,
            "description" to _description.value
        )
        db.collection("Items")
            .add(item)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                _isAdded.value = true
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error adding document", exception)
            }



    }

    companion object {
        const val TAG = "ItemViewModel"
    }


}