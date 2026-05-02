package com.example.fe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.R
import com.example.fe.model.MonAn

class OrderMenuAdapter(
    private var items: List<MonAn>,
    private val onQuantityChanged: () -> Unit
) : RecyclerView.Adapter<OrderMenuAdapter.ViewHolder>() {

    private val selectedItems = mutableMapOf<Int, Int>() // item_id to quantity

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvTenMon)
        val tvPrice: TextView = view.findViewById(R.id.tvGiaMon)
        val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
        val btnAdd: ImageButton = view.findViewById(R.id.btnAddQuantity)
        val btnRemove: ImageButton = view.findViewById(R.id.btnRemoveQuantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_menu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.text = item.tenMon
        holder.tvPrice.text = "${item.gia} VNĐ"
        
        val quantity = selectedItems[item.id] ?: 0
        holder.tvQuantity.text = quantity.toString()

        holder.btnAdd.setOnClickListener {
            selectedItems[item.id] = quantity + 1
            notifyItemChanged(position)
            onQuantityChanged()
        }

        holder.btnRemove.setOnClickListener {
            if (quantity > 0) {
                selectedItems[item.id] = quantity - 1
                notifyItemChanged(position)
                onQuantityChanged()
            }
        }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<MonAn>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    fun getSelectedItems(): List<SelectedOrderItem> {
        return items.filter { (selectedItems[it.id] ?: 0) > 0 }.map {
            SelectedOrderItem(it.id, selectedItems[it.id]!!)
        }
    }
}

data class SelectedOrderItem(val item_id: Int, val quantity: Int)
