package com.example.fe.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.R
import com.example.fe.model.Category

class CategoryFilterAdapter(
    private var categories: List<Category>,
    private val onCategoryClick: (Category?) -> Unit
) : RecyclerView.Adapter<CategoryFilterAdapter.ViewHolder>() {

    private var selectedPosition = 0

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvCategoryNameFilter)
        val card: CardView = view.findViewById(R.id.cardCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_filter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0) {
            holder.tvName.text = "Tất cả"
        } else {
            holder.tvName.text = categories[position - 1].name
        }

        if (selectedPosition == position) {
            holder.card.setCardBackgroundColor(Color.parseColor("#2196F3"))
            holder.tvName.setTextColor(Color.WHITE)
        } else {
            holder.card.setCardBackgroundColor(Color.WHITE)
            holder.tvName.setTextColor(Color.parseColor("#333333"))
        }

        holder.itemView.setOnClickListener {
            val oldPos = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(oldPos)
            notifyItemChanged(selectedPosition)
            
            if (selectedPosition == 0) {
                onCategoryClick(null)
            } else {
                onCategoryClick(categories[selectedPosition - 1])
            }
        }
    }

    override fun getItemCount() = categories.size + 1

    fun updateData(newCategories: List<Category>) {
        this.categories = newCategories
        notifyDataSetChanged()
    }
}
