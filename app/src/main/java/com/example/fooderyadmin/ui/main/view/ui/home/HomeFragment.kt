package com.example.fooderyadmin.ui.main.view.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fooderyadmin.R
import com.example.fooderyadmin.databinding.FragmentHomeBinding
import com.example.fooderyadmin.utils.Resource
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()

        initItems()
    }

    private fun setupUI() {

        binding.addItemButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_nav_home_to_addItemFragment)
        }

        binding.addCategoryButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_nav_home_to_addCategoryFragment)
        }

        itemAdapter = ItemAdapter()

        binding.itemRecyclerView.apply {
            adapter = itemAdapter
            layoutManager = LinearLayoutManager(activity)
        }


    }

    private fun initItems() {
        homeViewModel.foodItems.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    showLoading(false)
                    response.data?.let { itemResponse ->
                        itemAdapter.differ.submitList(itemResponse.toMutableList())
                    }
                }
                is Resource.Error -> {
                    response.message?.let { message ->
                        showLoading(false)
                        Log.e(TAG, "Error: $message")
                        Snackbar.make(binding.root, "Error:  $message", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }

                is Resource.Loading -> {
                    showLoading(true)
                    Log.d(TAG, "Loading...")
                }
            }
        })

        binding.swipeRefreshLayout.setOnRefreshListener {
            getFoodItems()
        }

        // reload if not success
        if (homeViewModel.foodItems.value !is Resource.Success) {
            getFoodItems()
        }

    }


    private fun getFoodItems() {
        homeViewModel.getFoodItems()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.swipeRefreshLayout.isRefreshing = isLoading
    }

    companion object {
        var TAG = "HomeFragment"
    }
}