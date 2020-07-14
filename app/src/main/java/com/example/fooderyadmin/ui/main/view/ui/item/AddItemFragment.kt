package com.example.fooderyadmin.ui.main.view.ui.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.fooderyadmin.R
import com.example.fooderyadmin.databinding.FragmentAddItemBinding
import com.google.android.material.snackbar.Snackbar

class AddItemFragment : Fragment() {
    private lateinit var itemViewModel: ItemViewModel
    private lateinit var binding: FragmentAddItemBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_item, container, false)

        itemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)
        binding.itemViewModel = itemViewModel

        binding.lifecycleOwner = this

        itemViewModel.isAdded.observe(viewLifecycleOwner, Observer { isItemAdded ->
            if (isItemAdded) {
                Snackbar.make(binding.root, "Added", Snackbar.LENGTH_SHORT)
            } else {
                Snackbar.make(binding.root, "Fail to add Item", Snackbar.LENGTH_SHORT)

            }
        })

        return binding.root
    }
}