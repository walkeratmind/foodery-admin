package com.example.fooderyadmin.ui.main.view.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fooderyadmin.R

class HomeViewModel : ViewModel() {

    private val _itemName = MutableLiveData<String>("")
    private val _itemType = MutableLiveData<String>("Non Veg.")
    private val _categoryName = MutableLiveData<String>("")
    private val _normalPrice = MutableLiveData<Int>()
    private val _offerPrice = MutableLiveData<Int>()
    private val _description = MutableLiveData<String>()


}