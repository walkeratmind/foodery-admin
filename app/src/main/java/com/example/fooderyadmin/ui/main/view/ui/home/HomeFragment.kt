package com.example.fooderyadmin.ui.main.view.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.fooderyadmin.R
import com.example.fooderyadmin.databinding.FragmentHomeBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)


        binding.addItemButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_nav_home_to_addItemFragment)
        }

        binding.addCategoryButton.setOnClickListener { view : View ->
            view.findNavController().navigate(R.id.action_nav_home_to_addCategoryFragment)
        }
        return binding.root
    }

}