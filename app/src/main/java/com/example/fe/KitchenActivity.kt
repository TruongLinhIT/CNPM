package com.example.fe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.fe.adapter.KitchenOrderAdapter
import com.example.fe.login.LoginActivity
import com.example.fe.model.UpdateStatusRequest
import com.example.fe.network.RetrofitClient
import kotlinx.coroutines.*

class KitchenActivity : AppCompatActivity() {

    private lateinit var rvOrders: RecyclerView
    private lateinit var adapter: KitchenOrderAdapter
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Senior Fix: Kích hoạt Edge-to-Edge và xử lý Insets để không bị đè bởi thanh trạng thái (Pin/Sóng)
        enableEdgeToEdge()
        setContentView(R.layout.activity_kitchen)

        val rootLayout = findViewById<android.widget.LinearLayout>(R.id.kitchenRoot)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rvOrders = findViewById(R.id.rvKitchenOrders)
        swipeRefresh = findViewById(R.id.swipeRefreshKitchen)
        val btnLogout = findViewById<Button>(R.id.btnKitchenLogout)

        setupRecyclerView()
        loadActiveOrders()

        swipeRefresh.setOnRefreshListener {
            loadActiveOrders()
        }

        btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = KitchenOrderAdapter(emptyList(), 
            onReadyClick = { order ->
                updateOrderStatus(order.order_id, "Ready")
            },
            onItemStatusClick = { detailId, status ->
                updateItemStatus(detailId, status)
            }
        )
        rvOrders.layoutManager = LinearLayoutManager(this)
        rvOrders.adapter = adapter
    }

    private fun loadActiveOrders() {
        swipeRefresh.isRefreshing = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getActiveOrders()
                withContext(Dispatchers.Main) {
                    swipeRefresh.isRefreshing = false
                    if (response.isSuccessful && response.body() != null) {
                        // Senior Fix: Bếp chỉ quan tâm các đơn có món ăn chưa hoàn thành (Pending, Preparing)
                        // Các món 'Ready' (đã nấu xong) và 'Served' (đã bưng đi) sẽ ẩn khỏi danh sách của bếp.
                        val allOrders = response.body()!!.data
                        val kitchenOrders = allOrders.filter { order ->
                            // Bếp chỉ quan tâm các món ăn (category != 1) chưa hoàn thành
                            order.orderDetails?.any { 
                                val isFood = it.menuItem?.category_id != 1
                                isFood && (it.status == "Pending" || it.status == "Preparing")
                            } == true
                        }
                        adapter.updateData(kitchenOrders)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    swipeRefresh.isRefreshing = false
                    Toast.makeText(this@KitchenActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateOrderStatus(orderId: Int, status: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.updateOrderStatus(orderId, UpdateStatusRequest(status))
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) loadActiveOrders()
                }
            } catch (e: Exception) { /* Log error */ }
        }
    }

    private fun updateItemStatus(detailId: Int, status: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.updateOrderDetailStatus(detailId, UpdateStatusRequest(status))
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) loadActiveOrders()
                }
            } catch (e: Exception) { /* Log error */ }
        }
    }
}
