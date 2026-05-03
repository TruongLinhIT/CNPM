package com.example.fe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.adapter.OrderDetailAdapter
import com.example.fe.model.*
import com.example.fe.network.RetrofitClient
import kotlinx.coroutines.*
import java.text.NumberFormat
import java.util.Locale

class TableDetailActivity : AppCompatActivity() {

    private lateinit var rvItems: RecyclerView
    private lateinit var adapter: OrderDetailAdapter
    private lateinit var tvSubtotal: TextView
    private lateinit var tvTax: TextView
    private lateinit var tvDiscount: TextView
    private lateinit var tvTotalAmount: TextView
    
    private var tableId: Int = -1
    private var tableNumber: Int = -1
    private var currentOrder: OrderData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table_detail)

        tableId = intent.getIntExtra("TABLE_ID", -1)
        tableNumber = intent.getIntExtra("TABLE_NUMBER", -1)

        val toolbar = findViewById<Toolbar>(R.id.toolbarTableDetail)
        toolbar.title = "Bàn $tableNumber - Chi tiết"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        rvItems = findViewById(R.id.rvOrderedItems)
        tvSubtotal = findViewById(R.id.tvSubtotal)
        tvTax = findViewById(R.id.tvTax)
        tvDiscount = findViewById(R.id.tvDiscount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)

        setupRecyclerView()
        loadTableOrder()

        findViewById<Button>(R.id.btnAddMore).setOnClickListener {
            val intent = Intent(this, OrderActivity::class.java)
            intent.putExtra("TABLE_ID", tableId)
            intent.putExtra("TABLE_NUMBER", tableNumber)
            intent.putExtra("ORDER_ID", currentOrder?.order_id ?: -1)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnPay).setOnClickListener {
            showPaymentDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = OrderDetailAdapter(emptyList()) { detail ->
            serveItem(detail.order_detail_id)
        }
        rvItems.layoutManager = LinearLayoutManager(this)
        rvItems.adapter = adapter
    }

    private fun loadTableOrder() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getActiveOrders()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val activeOrder = response.body()!!.data.find { it.table_id == tableId }
                        if (activeOrder != null) {
                            currentOrder = activeOrder
                            updateUI(activeOrder)
                        } else {
                            showStuckTableDialog()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@TableDetailActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showStuckTableDialog() {
        AlertDialog.Builder(this)
            .setTitle("Thông báo")
            .setMessage("Bàn $tableNumber đang báo 'Bận' nhưng không tìm thấy đơn hàng. Bạn có muốn dọn bàn này về trạng thái 'Trống' không?")
            .setPositiveButton("Dọn bàn") { _, _ -> forceClearTable() }
            .setNegativeButton("Hủy") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    private fun forceClearTable() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.updateTableStatus(tableId, UpdateStatusRequest("Available"))
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@TableDetailActivity, "Bàn $tableNumber đã sẵn sàng!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } catch (e: Exception) { /* Error handling */ }
        }
    }

    private fun updateUI(order: OrderData) {
        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        adapter.updateData(order.orderDetails ?: emptyList())
        tvSubtotal.text = formatter.format(order.subtotal)
        tvTax.text = formatter.format(order.tax)
        tvDiscount.text = "-${formatter.format(order.discount)}"
        tvTotalAmount.text = formatter.format(order.total_amount)
    }

    private fun serveItem(detailId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.updateOrderDetailStatus(detailId, UpdateStatusRequest(OrderStatus.SERVED))
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) loadTableOrder()
                }
            } catch (e: Exception) { /* Error handling */ }
        }
    }

    private fun showPaymentDialog() {
        // Senior Fix: Kiểm tra xem bếp đã nấu xong tất cả các món chưa
        val hasIncompleteItems = currentOrder?.orderDetails?.any { 
            it.status == "Pending" || it.status == "Preparing" 
        } ?: false

        if (hasIncompleteItems) {
            Toast.makeText(this, "Không thể thanh toán! Vui lòng đợi bếp nấu xong tất cả các món.", Toast.LENGTH_LONG).show()
            return
        }

        // Senior Fix: Chỉ giữ lại thanh toán Tiền mặt và ẩn các phương thức khác
        AlertDialog.Builder(this)
            .setTitle("Thanh toán hóa đơn")
            .setMessage("Xác nhận thanh toán TIỀN MẶT cho bàn $tableNumber?")
            .setPositiveButton("Xác nhận") { _, _ -> performPayment("Cash") }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun performPayment(method: String) {
        val orderId = currentOrder?.order_id ?: return
        val total = currentOrder?.total_amount ?: 0.0

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.createPayment(PaymentRequest(orderId, method, total))
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@TableDetailActivity, "Thanh toán thành công!", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this@TableDetailActivity, "Lỗi thanh toán từ server", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@TableDetailActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadTableOrder()
    }
}
