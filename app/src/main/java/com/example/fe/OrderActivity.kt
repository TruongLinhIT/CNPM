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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        tableId = intent.getIntExtra("TABLE_ID", -1)
        tableNumber = intent.getIntExtra("TABLE_NUMBER", -1)

        findViewById<TextView>(R.id.tvOrderTitle).text = "Đặt món - Bàn $tableNumber"
        rvMenu = findViewById(R.id.rvMenuOrder)
        tvTotalItems = findViewById(R.id.tvTotalItems)
        btnSubmit = findViewById(R.id.btnSubmitOrder)

        setupRecyclerView()
        loadMenu()

        btnSubmit.setOnClickListener {
            submitOrder()
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
                        val items = response.body()!!.data.map {
                            MonAn(it.item_id, it.name, it.price, it.image_url ?: "", it.description ?: "")
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

    private fun submitOrder() {
        val selectedItems = adapter.getSelectedItems()
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn món", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(this@OrderActivity, "Gửi đơn hàng thành công", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@OrderActivity, "Lỗi gửi đơn hàng", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@OrderActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
