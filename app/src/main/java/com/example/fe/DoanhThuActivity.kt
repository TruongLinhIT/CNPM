package com.example.fe

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.adapter.DonHangAdapter
import com.example.fe.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.*

class DoanhThuActivity : AppCompatActivity() {

    private lateinit var tvTongDoanhThu: TextView
    private lateinit var rvDonHang: RecyclerView
    private lateinit var adapter: DonHangAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doanh_thu)

        tvTongDoanhThu = findViewById(R.id.tvTongDoanhThu)
        rvDonHang = findViewById(R.id.rvDonHang)

        setupRecyclerView()
        loadData()
    }

    private fun setupRecyclerView() {
        adapter = DonHangAdapter(emptyList())
        rvDonHang.layoutManager = LinearLayoutManager(this)
        rvDonHang.adapter = adapter
    }

    private fun loadData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 1. Lấy báo cáo doanh thu tổng quát
                val revenueResponse = RetrofitClient.instance.getRevenueReport()
                
                // 2. Lấy danh sách toàn bộ đơn hàng để làm lịch sử
                val ordersResponse = RetrofitClient.instance.getOrders()

                withContext(Dispatchers.Main) {
                    if (revenueResponse.isSuccessful && revenueResponse.body() != null) {
                        val revenueData = revenueResponse.body()!!.data
                        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                        tvTongDoanhThu.text = formatter.format(revenueData.total_revenue)
                    }

                    if (ordersResponse.isSuccessful && ordersResponse.body() != null) {
                        val orders = ordersResponse.body()!!.data
                        // Sắp xếp đơn mới nhất lên đầu
                        adapter.updateData(orders.sortedByDescending { it.order_id })
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DoanhThuActivity, "Lỗi tải dữ liệu: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
