package com.example.fe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.R
import com.example.fe.model.OrderDetailData
import com.example.fe.model.OrderStatus
import java.text.NumberFormat
import java.util.Locale

class OrderDetailAdapter(
    private var items: List<OrderDetailData>,
    private val onServeClick: (OrderDetailData) -> Unit
) : RecyclerView.Adapter<OrderDetailAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvItemName)
        val tvStatus: TextView = view.findViewById(R.id.tvItemStatus)
        val btnServe: Button = view.findViewById(R.id.btnServeItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        
        holder.tvName.text = "${item.menuItem?.name} x ${item.quantity}"
        
        val price = formatter.format(item.price_at_time * item.quantity)
        holder.tvStatus.text = "Trạng thái: ${item.status} | $price"
        
        // Cài đặt màu sắc theo trạng thái
        holder.tvStatus.setTextColor(when(item.status) {
            OrderStatus.PENDING -> 0xFFFF0000.toInt() // Đỏ
            OrderStatus.PREPARING -> 0xFF0000FF.toInt() // Xanh dương
            OrderStatus.READY -> 0xFF4CAF50.toInt() // Xanh lá
            OrderStatus.SERVED -> 0xFF757575.toInt() // Xám
            else -> 0xFF000000.toInt()
        })

        // Nút Phục vụ chỉ hiện khi món đã READY
        holder.btnServe.visibility = if (item.status == OrderStatus.READY) View.VISIBLE else View.GONE
        holder.btnServe.setOnClickListener { onServeClick(item) }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<OrderDetailData>) {
        items = newItems
        notifyDataSetChanged()
    }
}
