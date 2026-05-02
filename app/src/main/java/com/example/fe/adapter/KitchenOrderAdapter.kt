package com.example.fe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.R
import com.example.fe.model.OrderData

class KitchenOrderAdapter(
    private var orders: List<OrderData>,
    private val onReadyClick: (OrderData) -> Unit,
    private val onItemStatusClick: (Int, String) -> Unit
) : RecyclerView.Adapter<KitchenOrderAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTable: TextView = view.findViewById(R.id.tvKitchenOrderTable)
        val tvStatus: TextView = view.findViewById(R.id.tvKitchenOrderStatus)
        val tvTime: TextView = view.findViewById(R.id.tvKitchenOrderTime)
        val llItems: LinearLayout = view.findViewById(R.id.llKitchenItems)
        val btnReady: Button = view.findViewById(R.id.btnKitchenReady)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_kitchen_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        holder.tvTable.text = "Bàn ${order.DiningTable?.number ?: order.table_id}"
        holder.tvStatus.text = order.status
        holder.tvTime.text = order.created_at

        holder.llItems.removeAllViews()
        order.OrderDetails?.forEach { detail ->
            val itemView = LayoutInflater.from(holder.itemView.context).inflate(android.R.layout.simple_list_item_2, holder.llItems, false)
            val text1 = itemView.findViewById<TextView>(android.R.id.text1)
            val text2 = itemView.findViewById<TextView>(android.R.id.text2)
            
            text1.text = "${detail.MenuItem?.name} x ${detail.quantity}"
            text2.text = "Trạng thái: ${detail.status}"
            
            // Màu sắc theo trạng thái món
            text2.setTextColor(when(detail.status) {
                "Pending" -> 0xFFFF0000.toInt() // Đỏ
                "Preparing" -> 0xFF0000FF.toInt() // Xanh dương
                "Ready" -> 0xFF008000.toInt() // Xanh lá
                else -> 0xFF666666.toInt()
            })

            itemView.setOnClickListener {
                val nextStatus = when(detail.status) {
                    "Pending" -> "Preparing"
                    "Preparing" -> "Ready"
                    else -> null
                }
                nextStatus?.let { status ->
                    onItemStatusClick(detail.order_detail_id, status)
                }
            }
            holder.llItems.addView(itemView)
        }

        holder.btnReady.setOnClickListener { onReadyClick(order) }
        
        // Chỉ hiện nút "Hoàn thành" nếu toàn bộ món đã Ready
        val allReady = order.OrderDetails?.all { it.status == "Ready" || it.status == "Served" } ?: false
        holder.btnReady.visibility = if (allReady) View.VISIBLE else View.GONE
    }

    override fun getItemCount() = orders.size

    fun updateData(newOrders: List<OrderData>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}
