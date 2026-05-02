package com.example.fe.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.R
import com.example.fe.model.Table

class TableAdapter(
    private var tables: List<Table>,
    private val onStatusClick: (Table) -> Unit
) : RecyclerView.Adapter<TableAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNumber: TextView = view.findViewById(R.id.tvNumber)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val btnAction: Button = view.findViewById(R.id.btnAction)
        val layoutItem: View = view.findViewById(R.id.layoutItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_table, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val table = tables[position]
        holder.tvNumber.text = "Bàn ${table.number}"
        holder.tvStatus.text = table.status

        // Đổi màu dựa trên trạng thái (Available/Occupied)
        if (table.status == "Available") {
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50")) // Green
            holder.btnAction.text = "Mở bàn"
        } else {
            holder.tvStatus.setTextColor(Color.parseColor("#F44336")) // Red
            holder.btnAction.text = "Dọn bàn"
        }

        holder.btnAction.setOnClickListener { onStatusClick(table) }
    }

    override fun getItemCount() = tables.size

    fun updateData(newTables: List<Table>) {
        this.tables = newTables
        notifyDataSetChanged()
    }
}
