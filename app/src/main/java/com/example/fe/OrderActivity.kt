package com.example.fe

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.adapter.OrderMenuAdapter
import com.example.fe.model.*
import com.example.fe.network.RetrofitClient
import kotlinx.coroutines.*

class OrderActivity : AppCompatActivity() {

    private lateinit var rvMenu: RecyclerView
    private lateinit var adapter: OrderMenuAdapter
    private lateinit var tvTotalItems: TextView
    private lateinit var btnSubmit: Button
    
    private var tableId: Int = -1
    private var tableNumber: Int = -1
    private var existingOrderId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        tableId = intent.getIntExtra("TABLE_ID", -1)
        tableNumber = intent.getIntExtra("TABLE_NUMBER", -1)
        existingOrderId = intent.getIntExtra("ORDER_ID", -1)

        val titlePrefix = if (existingOrderId != -1) "Gọi thêm" else "Đặt món"
        findViewById<TextView>(R.id.tvOrderTitle).text = "$titlePrefix - Bàn $tableNumber"
        
        rvMenu = findViewById(R.id.rvMenuOrder)
        tvTotalItems = findViewById(R.id.tvTotalItems)
        btnSubmit = findViewById(R.id.btnSubmitOrder)

        setupRecyclerView()
        loadMenu()

        btnSubmit.setOnClickListener {
            if (existingOrderId != -1) {
                addMoreItems()
            } else {
                submitNewOrder()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = OrderMenuAdapter(emptyList()) {
            updateTotalUI()
        }
        rvMenu.layoutManager = LinearLayoutManager(this)
        rvMenu.adapter = adapter
    }

    private fun loadMenu() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getMenuItems()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val menuItems: List<MenuItemData> = response.body()!!.data
                        val items = menuItems.map { item ->
                            MonAn(
                                id = item.item_id,
                                tenMon = item.name,
                                gia = item.price,
                                hinhAnh = item.image_url ?: "",
                                moTa = item.description ?: "",
                                category_id = item.category_id ?: 0
                            )
                        }
                        adapter.updateData(items)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@OrderActivity, "Lỗi tải thực đơn", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateTotalUI() {
        val totalCount = adapter.getSelectedItems().sumOf { it.quantity }
        tvTotalItems.text = "Đã chọn: $totalCount món"
    }

    private fun submitNewOrder() {
        val selectedItems = adapter.getSelectedItems()
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất 1 món", Toast.LENGTH_SHORT).show()
            return
        }

        // Tạo request đơn giản (user_id mặc định null nếu server cho phép hoặc tùy chỉnh sau)
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
        val selectedItems = adapter.getSelectedItems()
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
