package com.example.fe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.adapter.TableAdapter
import com.example.fe.login.LoginActivity
import com.example.fe.model.Table
import com.example.fe.network.RetrofitClient
import kotlinx.coroutines.*

class WaitstaffActivity : AppCompatActivity() {
    private lateinit var rvTables: RecyclerView
    private lateinit var adapter: TableAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waitstaff)

        rvTables = findViewById(R.id.rvTablesWaitstaff)
        val btnLogout = findViewById<Button>(R.id.btnLogoutWaitstaff)

        setupRecyclerView()
        loadTableList()

        btnLogout.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
    }

    private fun setupRecyclerView() {
        adapter = TableAdapter(emptyList()) { table ->
            if (table.status == "Available") {
                // Nếu bàn trống -> Đi tới đặt món (Sẽ tạo Order mới)
                val intent = Intent(this, OrderActivity::class.java)
                intent.putExtra("TABLE_ID", table.id)
                intent.putExtra("TABLE_NUMBER", table.number)
                startActivity(intent)
            } else {
                // Nếu bàn có khách -> Đi tới chi tiết bàn để Xem/Thêm món/Thanh toán
                val intent = Intent(this, TableDetailActivity::class.java)
                intent.putExtra("TABLE_ID", table.id)
                intent.putExtra("TABLE_NUMBER", table.number)
                startActivity(intent)
            }
        }
        rvTables.layoutManager = GridLayoutManager(this, 2)
        rvTables.adapter = adapter
    }

    private fun loadTableList() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.getTables()
                }
                if (response.isSuccessful) {
                    response.body()?.data?.let { adapter.updateData(it) }
                }
            } catch (e: Exception) {
                Toast.makeText(this@WaitstaffActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadTableList() // Cập nhật lại trạng thái bàn sau khi quay lại
    }
}
