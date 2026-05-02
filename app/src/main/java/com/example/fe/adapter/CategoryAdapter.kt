package com.example.fe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.R
import com.example.fe.model.Category

class CategoryAdapter(
    private var categories: List<Category>,
    private val onEditClick: (Category) -> Unit,
    private val onDeleteClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvCategoryName)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEditCategory)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDeleteCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.tvName.text = category.name
        holder.btnEdit.setOnClickListener { onEditClick(category) }
        holder.btnDelete.setOnClickListener { onDeleteClick(category) }
    }

    override fun getItemCount() = categories.size

    fun updateData(newCategories: List<Category>) {
        this.categories = newCategories
        notifyDataSetChanged()
    }
}
