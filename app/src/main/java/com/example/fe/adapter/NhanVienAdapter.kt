package com.example.fe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.R
import com.example.fe.model.NhanVien

class NhanVienAdapter(
    private var danhSachNhanVien: MutableList<NhanVien>,
    private val onEditClick: (NhanVien) -> Unit,
    private val onDeleteClick: (NhanVien) -> Unit
) : RecyclerView.Adapter<NhanVienAdapter.NhanVienViewHolder>() {

    class NhanVienViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHoTen: TextView = itemView.findViewById(R.id.tvHoTen)
        val tvRole: TextView = itemView.findViewById(R.id.tvRole)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NhanVienViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_nhan_vien, parent, false)
        return NhanVienViewHolder(view)
    }

    override fun onBindViewHolder(holder: NhanVienViewHolder, position: Int) {
        val nhanVien = danhSachNhanVien[position]
        holder.tvHoTen.text = nhanVien.hoTen
        holder.tvRole.text = "Vai trò: ${nhanVien.role}"

        holder.btnEdit.setOnClickListener { onEditClick(nhanVien) }
        holder.btnDelete.setOnClickListener { onDeleteClick(nhanVien) }
    }

    override fun getItemCount(): Int = danhSachNhanVien.size

    fun updateData(newList: List<NhanVien>) {
        danhSachNhanVien.clear()
        danhSachNhanVien.addAll(newList)
        notifyDataSetChanged()
    }
}