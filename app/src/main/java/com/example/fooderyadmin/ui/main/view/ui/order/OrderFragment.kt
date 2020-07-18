package com.example.fooderyadmin.ui.main.view.ui.order

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.foodery.utils.show
import com.example.foodery.utils.showSnackbar
import com.example.fooderyadmin.R
import com.example.fooderyadmin.data.model.Order
import com.example.fooderyadmin.databinding.FragmentOrderBinding
import com.example.fooderyadmin.utils.Constants
import com.example.fooderyadmin.utils.Resource
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.layout_order_dialog.*
import kotlinx.android.synthetic.main.layout_order_item.view.*


class OrderFragment : Fragment() {
    private lateinit var orderViewModel: OrderViewModel
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var fragmentOrderBinding: FragmentOrderBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var user: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        orderViewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        fragmentOrderBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_order, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth.currentUser!!

        return fragmentOrderBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()

        // onItem Clicked
        orderAdapter.setOnClickListener { order ->
            showOrderDialog(order)
        }

        initItems()
    }

    private fun showOrderDialog(order: Order) {
        val dialog = MaterialDialog(requireContext())
            .customView(R.layout.layout_order_dialog, scrollable = true)

        dialog.itemNameView.text = order.itemName

        orderStatusTextView.text = requireContext().getString(R.string.order_status_string, order.status)

        if (order.unitPrice!! > 500) {
            dialog.normalPriceView.setBackgroundResource(R.drawable.price_tag_high_bg)
        }
        dialog.normalPriceView.text =
            requireContext().getString(R.string.price_string, order.unitPrice)


        dialog.quantityTextView.text =
            requireContext().getString(R.string.quantity_string, order.totalItem)
        dialog.totalPriceValue.text = requireContext().getString(
            R.string.price_string, order.totalPrice
        )

        if (!order.userName.equals("")) {
            dialog.userFullName.apply {
                text = order.userName
                show()
            }
        }

        if (!order.userEmail.equals("")) {
            dialog.userEmail.apply {
                text = order.userEmail
                show()
            }
        }

        if (!order.userPhone.equals("")) {
            dialog.userPhoneTextView.apply {
                text = order.userPhone
                show()
            }
        }


        val db = FirebaseFirestore.getInstance()

        dialog.acceptOrderBtn.setOnClickListener { view ->
            dialog.dismiss()

            /*
                val id:String,
                val itemId: String,
                val userId: String,
                val totalPrice: String,
                val orderDate: String,
                val status: String  //
            */

            val orderItem =
                Order(
                    null,
                    order.userId,
                    order.userName,
                    order.userEmail,
                    order.userPhone,
                    order.itemId,
                    order.itemName,
                    order.unitPrice,
                    null,
                    null,
                    order.totalItem,
                    order.totalPrice,
                    order.orderDate,
                    "on process"
                )

            showLoading(true)
            db.collection(Constants.USER_REF)
                .document(Constants.ORDER_REF).collection(Constants.ORDER_REF).document(order.id.toString())
                .set(orderItem, SetOptions.merge())
                .addOnSuccessListener { documentReference ->
                    showLoading(false)
                    getFoodItems()
                    Log.d(TAG, "DocumentSnapshot added: $documentReference")
                    requireActivity().showSnackbar(requireView(), getString(R.string.successful))
                }
                .addOnFailureListener { exception ->
                    showLoading(false)
                    Log.w(TAG, "Error adding document", exception)
                    requireActivity().showSnackbar(requireView(), getString(R.string.failed))
                }
        }

        dialog.show()
    }


    private fun setupUI() {

        orderAdapter = OrderAdapter()

        fragmentOrderBinding.orderRecyclerView.apply {
            adapter = orderAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun initItems() {
        orderViewModel.orderItems.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        showLoading(false)
                        response.data?.let { orderResponse ->
                            orderAdapter.differ.submitList(orderResponse.toMutableList())
                        }
                    }
                    is Resource.Error -> {
                        response.message?.let { message ->
                            showLoading(false)
                            Log.e(TAG, "Error: $message")
                            Snackbar.make(
                                fragmentOrderBinding.root,
                                "Error:  $message",
                                Snackbar.LENGTH_SHORT
                            )
                                .show()
                        }
                    }

                    is Resource.Loading -> {
                        showLoading(true)
                        Log.d(TAG, "Loading...")
                    }
                }
            })

        fragmentOrderBinding.swipeRefreshLayout.setOnRefreshListener {
            getFoodItems()
        }

        // reload if not success
        if (orderViewModel.orderItems.value !is Resource.Success) {
            getFoodItems()
        }

    }


    private fun getFoodItems() {
        orderViewModel.getOrderItems()
    }

    private fun showLoading(isLoading: Boolean) {
        fragmentOrderBinding.swipeRefreshLayout.isRefreshing = isLoading
    }

    companion object {
        var TAG = "OrderFragment"
    }

}