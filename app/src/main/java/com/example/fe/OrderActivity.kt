package com.example.fe

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.adapter.CategoryFilterAdapter
import com.example.fe.adapter.OrderMenuAdapter
import com.example.fe.model.*
import com.example.fe.network.RetrofitClient
import kotlinx.coroutines.*

class OrderActivity : AppCompatActivity() {

    private lateinit var rvMenu: RecyclerView
    private lateinit var rvCategories: RecyclerView
    private lateinit var menuAdapter: OrderMenuAdapter
    private lateinit var categoryAdapter: CategoryFilterAdapter
    private lateinit var tvTotalItems: TextView
    private lateinit var btnSubmit: Button
    
    private var tableId: Int = -1
    private var tableNumber: Int = -1
    private var existingOrderId: Int = -1
    
    private var fullMenuList: List<MonAn> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        tableId = intent.getIntExtra("TABLE_ID", -1)
        tableNumber = intent.getIntExtra("TABLE_NUMBER", -1)
        existingOrderId = intent.getIntExtra("ORDER_ID", -1)

        val titlePrefix = if (existingOrderId != -1) "Gọi thêm" else "Đặt món"
        findViewById<TextView>(R.id.tvOrderTitle).text = "$titlePrefix - Bàn $tableNumber"
        
        rvMenu = findViewById(R.id.rvMenuOrder)
        rvCategories = findViewById(R.id.rvCategoriesFilter)
        tvTotalItems = findViewById(R.id.tvTotalItems)
        btnSubmit = findViewById(R.id.btnSubmitOrder)

        setupRecyclerViews()
        loadCategories()
        loadMenu()

        btnSubmit.setOnClickListener {
            if (existingOrderId != -1) {
                addMoreItems()
            } else {
                submitNewOrder()
            }
        }
    }

    private fun setupRecyclerViews() {
        // Menu Adapter
        menuAdapter = OrderMenuAdapter(emptyList()) {
            updateTotalUI()
        }
        rvMenu.layoutManager = LinearLayoutManager(this)
        rvMenu.adapter = menuAdapter

        // Category Adapter
        categoryAdapter = CategoryFilterAdapter(emptyList()) { category ->
            filterMenuByCategory(category)
        }
        rvCategories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvCategories.adapter = categoryAdapter
    }

    private fun loadCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getCategories()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        categoryAdapter.updateData(response.body()!!.data)
                    }
                }
            } catch (e: Exception) {
                // Silent fail or toast
            }
        }
    }

    private fun loadMenu() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getMenuItems()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val menuItems: List<MenuItemData> = response.body()!!.data
                        // Chỉ lấy những món còn hàng (is_available == true)
                        fullMenuList = menuItems
                            .filter { it.is_available }
                            .map { item ->
                                MonAn(
                                    id = item.item_id,
                                    tenMon = item.name,
                                    gia = item.price,
                                    hinhAnh = item.image_url ?: "",
                                    moTa = item.description ?: "",
                                    category_id = item.category_id ?: 0,
                                    isAvailable = item.is_available
                                )
                            }
                        menuAdapter.updateData(fullMenuList)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@OrderActivity, "Lỗi tải thực đơn", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun filterMenuByCategory(category: Category?) {
        if (category == null) {
            menuAdapter.updateData(fullMenuList)
        } else {
            val filtered = fullMenuList.filter { it.category_id == category.category_id }
            menuAdapter.updateData(filtered)
        }
    }

    private fun updateTotalUI() {
        val totalCount = menuAdapter.getSelectedItems().sumOf { it.quantity }
        tvTotalItems.text = "Đã chọn: $totalCount món"
    }

    private fun submitNewOrder() {
        val selectedItems = menuAdapter.getSelectedItems()
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất 1 món", Toast.LENGTH_SHORT).show()
            return
        }

        val request = CreateOrderRequest(
            table_id = tableId,
            items = selectedItems.map { OrderItemRequest(it.item_id, it.quantity) }
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.createOrder(request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@OrderActivity, "Tạo đơn hàng thành công!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: "Lỗi từ Server"
                        Toast.makeText(this@OrderActivity, "Lỗi (${response.code()}): $errorMsg", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@OrderActivity, "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addMoreItems() {
        val selectedItems = menuAdapter.getSelectedItems()
        if (selectedItems.isEmpty()) return

        val request = AddItemsRequest(
            items = selectedItems.map { OrderItemRequest(it.item_id, it.quantity) }
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.addOrderItems(existingOrderId, request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@OrderActivity, "Đã thêm món vào đơn", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@OrderActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
