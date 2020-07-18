package com.example.fooderyadmin.ui.main.view.ui.order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.foodery.utils.show
import com.example.fooderyadmin.R
import com.example.fooderyadmin.data.model.Order
import kotlinx.android.synthetic.main.layout_item.view.itemNameView
import kotlinx.android.synthetic.main.layout_item.view.normalPriceView
import kotlinx.android.synthetic.main.layout_order_dialog.*
import kotlinx.android.synthetic.main.layout_order_item.view.*

class OrderAdapter : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        return OrderViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_order_item, parent, false)
        )
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = differ.currentList[position]
        holder.itemView.apply {
            itemNameView.text = order.itemName

            orderStatusTextView.text = context.getString(R.string.order_status_string, order.status)

            if (order.unitPrice!! > 500) {
                normalPriceView.setBackgroundResource(R.drawable.price_tag_high_bg)
            }
            normalPriceView.text = context.getString(R.string.price_string, order.unitPrice)

            quantityTextView.text = order.totalItem.toString()
            totalPriceValue.text = context.getString(R.string.price_string, order.totalItem)
            if (!order.location.equals("")) {
                userLocationValue.apply {
                    text = context.getString(R.string.location_string, order.location)
                    show()
                }
            }

            orderDate.text = context.getString(R.string.date_string, order.orderDate)

            if (!order.userName.equals("")) {
                userFullName.apply {
                    text = order.userName
                    show()
                }
            }

            if (!order.userEmail.equals("")) {
                userEmail.apply {
                    text = order.userEmail
                    show()
                }
            }

            if (!order.userPhone.equals("")) {
                userPhoneTextView.apply {
                    text = order.userPhone
                    show()
                }
            }

            setOnClickListener {
                onItemClickListener?.let { it(order) }
            }
        }
    }

    private var onItemClickListener: ((Order) -> Unit)? = null

    fun setOnClickListener(listener: (Order) -> Unit) {
        onItemClickListener = listener
    }


    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Order>() {
            override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
                return oldItem == newItem
            }

        }
    }
}