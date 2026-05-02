package com.example.fe

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.adapter.CategoryAdapter
import com.example.fe.model.*
import com.example.fe.network.RetrofitClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuanLyDanhMucActivity : AppCompatActivity() {

    private lateinit var rvCategories: RecyclerView
    private lateinit var adapter: CategoryAdapter
    private val categoriesList = mutableListOf<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quan_ly_danh_muc)

        rvCategories = findViewById(R.id.rvCategories)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAddCategory)

        setupRecyclerView()
        loadCategories()

        fabAdd.setOnClickListener {
            showCategoryDialog(null)
        }
    }

    private fun setupRecyclerView() {
        adapter = CategoryAdapter(categoriesList,
            onEditClick = { category -> showCategoryDialog(category) },
            onDeleteClick = { category -> deleteCategoryFromList(category) }
        )
        rvCategories.layoutManager = LinearLayoutManager(this)
        rvCategories.adapter = adapter
    }

    private fun loadCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getCategories()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()!!
                        if (body.success) {
                            categoriesList.clear()
                            categoriesList.addAll(body.data)
                            adapter.updateData(categoriesList)
                        }
                    } else {
                        Toast.makeText(this@QuanLyDanhMucActivity, "Lỗi tải danh mục", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@QuanLyDanhMucActivity, "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showCategoryDialog(category: Category?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(if (category == null) "Thêm danh mục" else "Sửa danh mục")

        val input = EditText(this)
        input.hint = "Tên danh mục"
        if (category != null) {
            input.setText(category.name)
        }
        builder.setView(input)

        builder.setPositiveButton("Lưu") { _, _ ->
            val name = input.text.toString().trim()
            if (name.isNotEmpty()) {
                if (category == null) {
                    performCreateCategory(name)
                } else {
                    performUpdateCategory(category.category_id, name)
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Hủy", null)
        builder.show()
    }

    private fun performCreateCategory(name: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.createCategory(CategoryRequest(name))
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@QuanLyDanhMucActivity, "Đã thêm", Toast.LENGTH_SHORT).show()
                        loadCategories()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@QuanLyDanhMucActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun performUpdateCategory(id: Int, name: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.updateCategory(id, CategoryRequest(name))
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@QuanLyDanhMucActivity, "Đã cập nhật", Toast.LENGTH_SHORT).show()
                        loadCategories()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@QuanLyDanhMucActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteCategoryFromList(category: Category) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa danh mục '${category.name}'?")
            .setPositiveButton("Xóa") { _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = RetrofitClient.instance.deleteCategory(category.category_id)
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@QuanLyDanhMucActivity, "Đã xóa", Toast.LENGTH_SHORT).show()
                                loadCategories()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@QuanLyDanhMucActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}
