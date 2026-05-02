package com.example.fe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.R
import com.example.fe.model.MonAn
import java.text.NumberFormat
import java.util.Locale

class MonAnAdapter(
    private var listMonAn: MutableList<MonAn>,
    private val onEditClick: (MonAn) -> Unit,
    private val onDeleteClick: (MonAn) -> Unit
) : RecyclerView.Adapter<MonAnAdapter.MonAnViewHolder>() {

    class MonAnViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgMonAn: ImageView = view.findViewById(R.id.imgMonAn)
        val tvTenMon: TextView = view.findViewById(R.id.tvTenMon)
        val tvGiaMon: TextView = view.findViewById(R.id.tvGiaMon)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEditMon)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDeleteMon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonAnViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mon_an, parent, false)
        return MonAnViewHolder(view)
    }

    override fun onBindViewHolder(holder: MonAnViewHolder, position: Int) {
        val monAn = listMonAn[position]
        holder.tvTenMon.text = monAn.tenMon
        
        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        holder.tvGiaMon.text = formatter.format(monAn.gia)

        holder.btnEdit.setOnClickListener { onEditClick(monAn) }
        holder.btnDelete.setOnClickListener { onDeleteClick(monAn) }
    }

    override fun getItemCount() = listMonAn.size

    fun updateData(newList: List<MonAn>) {
        listMonAn.clear()
        listMonAn.addAll(newList)
        notifyDataSetChanged()
    }
}