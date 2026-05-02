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
                // Nếu bàn trống -> Hỏi xác nhận mở bàn và đi tới đặt món
                openTableAndOrder(table)
            } else {
                // Nếu bàn có khách -> Đi tới quản lý đơn hàng bàn đó
                val intent = Intent(this, OrderActivity::class.java)
                intent.putExtra("TABLE_ID", table.id)
                intent.putExtra("TABLE_NUMBER", table.number)
                startActivity(intent)
            }
        }
        rvTables.layoutManager = GridLayoutManager(this, 2)
        rvTables.adapter = adapter
    }

    private fun openTableAndOrder(table: Table) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.updateTableStatus(table.id, mapOf("status" to "Occupied"))
                }
                if (response.isSuccessful) {
                    val intent = Intent(this@WaitstaffActivity, OrderActivity::class.java)
                    intent.putExtra("TABLE_ID", table.id)
                    intent.putExtra("TABLE_NUMBER", table.number)
                    startActivity(intent)
                    loadTableList()
                }
            } catch (e: Exception) {
                Toast.makeText(this@WaitstaffActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
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
