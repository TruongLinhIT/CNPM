package com.example.fe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.R
import com.example.fe.model.OrderData
import java.text.NumberFormat
import java.util.Locale

class DonHangAdapter(private var orders: List<OrderData>) : RecyclerView.Adapter<DonHangAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvId: TextView = view.findViewById(android.R.id.text1)
        val tvInfo: TextView = view.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        
        holder.tvId.text = "Mã đơn: #${order.order_id} - Bàn ${order.diningTable?.number ?: order.table_id}"
        holder.tvInfo.text = "Tổng: ${formatter.format(order.total_amount)} | Ngày: ${order.created_at}"
    }

    override fun getItemCount() = orders.size

    fun updateData(newOrders: List<OrderData>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}
