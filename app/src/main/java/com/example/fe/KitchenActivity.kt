package com.example.fe

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.fe.adapter.KitchenOrderAdapter
import com.example.fe.model.UpdateStatusRequest
import com.example.fe.network.RetrofitClient
import kotlinx.coroutines.*

class KitchenActivity : AppCompatActivity() {

    private lateinit var rvOrders: RecyclerView
    private lateinit var adapter: KitchenOrderAdapter
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kitchen)

        rvOrders = findViewById(R.id.rvKitchenOrders)
        swipeRefresh = findViewById(R.id.swipeRefreshKitchen)

        setupRecyclerView()
        loadActiveOrders()

        swipeRefresh.setOnRefreshListener {
            loadActiveOrders()
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
                val response = RetrofitClient.instance.getOrders()
                withContext(Dispatchers.Main) {
                    swipeRefresh.isRefreshing = false
                    if (response.isSuccessful && response.body() != null) {
                        // Lọc các đơn hàng đang chờ hoặc đang chế biến
                        val activeOrders = response.body()!!.data.filter { 
                            it.status == "Pending" || it.status == "Preparing" 
                        }
                        adapter.updateData(activeOrders)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    swipeRefresh.isRefreshing = false
                    Toast.makeText(this@KitchenActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateOrderStatus(orderId: Int, status: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.updateOrderStatus(orderId, UpdateStatusRequest(status))
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        loadActiveOrders()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@KitchenActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateItemStatus(detailId: Int, status: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.updateOrderDetailStatus(detailId, UpdateStatusRequest(status))
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        loadActiveOrders()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@KitchenActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
