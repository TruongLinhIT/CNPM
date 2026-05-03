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
        // btnReady is no longer used as per user request
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_kitchen_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        holder.tvTable.text = "Bàn ${order.diningTable?.number ?: order.table_id}"
        holder.tvStatus.text = order.status
        holder.tvTime.text = order.created_at

        holder.llItems.removeAllViews()
        order.orderDetails?.forEach { detail ->
            val itemView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.item_kitchen_detail, holder.llItems, false)
            
            val tvName = itemView.findViewById<TextView>(R.id.tvDetailName)
            val tvStatus = itemView.findViewById<TextView>(R.id.tvDetailStatus)
            val btnDone = itemView.findViewById<Button>(R.id.btnDoneItem)
            
            tvName.text = "${detail.menuItem?.name} x ${detail.quantity}"
            tvStatus.text = "Trạng thái: ${detail.status}"
            
            // Colors for status
            when(detail.status) {
                "Pending" -> tvStatus.setTextColor(0xFFFF0000.toInt())
                "Preparing" -> tvStatus.setTextColor(0xFF0000FF.toInt())
                "Ready" -> tvStatus.setTextColor(0xFF008000.toInt())
            }

            // Hide "XONG" button if already Ready or Served
            if (detail.status == "Ready" || detail.status == "Served") {
                btnDone.visibility = View.GONE
            } else {
                btnDone.visibility = View.VISIBLE
                btnDone.setOnClickListener {
                    // Senior Fix: Transition directly to "Ready" instead of "Preparing"
                    onItemStatusClick(detail.order_detail_id, "Ready")
                }
            }
            
            holder.llItems.addView(itemView)
        }
        
        // Removed btnReady logic as per user request to hide "Complete All" button
    }

    override fun getItemCount() = orders.size

    fun updateData(newOrders: List<OrderData>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}
