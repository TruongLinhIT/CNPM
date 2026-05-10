package com.example.fe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.adapter.TableAdapter
import com.example.fe.model.*
import com.example.fe.network.RetrofitClient
import kotlinx.coroutines.*

class TableActivity : AppCompatActivity() {
    private lateinit var rvTables: RecyclerView
    private lateinit var adapter: TableAdapter
    private var userRole: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table)

        userRole = intent.getStringExtra("USER_ROLE")?.lowercase()

        rvTables = findViewById(R.id.rvTables)
        val fabAdd = findViewById<View>(R.id.fabAddTable)

        if (userRole == "manager" || userRole == "admin") {
            fabAdd.visibility = View.VISIBLE
            fabAdd.setOnClickListener { showAddTableDialog() }
        } else {
            fabAdd.visibility = View.GONE
        }

        setupRecyclerView()
    }

    // Senior Fix: Tự động tải lại danh sách bàn mỗi khi màn hình hiển thị lại
    override fun onResume() {
        super.onResume()
        loadTableList()
    }

    private fun setupRecyclerView() {
        adapter = TableAdapter(emptyList()) { table ->
            // Khi click vào bàn, mở màn hình chi tiết hoặc xử lý theo luồng của app
            val intent = android.content.Intent(this, TableDetailActivity::class.java)
            intent.putExtra("TABLE_ID", table.id)
            intent.putExtra("TABLE_NUMBER", table.number)
            startActivity(intent)
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
                if (response.isSuccessful && response.body() != null) {
                    adapter.updateData(response.body()!!.data)
                }
            } catch (e: Exception) {
                Toast.makeText(this@TableActivity, "Lỗi tải bàn: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddTableDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_table, null)
        val edtNumber = view.findViewById<EditText>(R.id.edtTableNumber)
        val edtCapacity = view.findViewById<EditText>(R.id.edtCapacity)

        AlertDialog.Builder(this)
            .setTitle("Thêm bàn mới")
            .setView(view)
            .setPositiveButton("Thêm") { _, _ ->
                val numberStr = edtNumber.text.toString()
                val capacityStr = edtCapacity.text.toString()
                if (numberStr.isNotEmpty() && capacityStr.isNotEmpty()) {
                    createNewTable(numberStr.toInt(), capacityStr.toInt())
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun createNewTable(number: Int, capacity: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitClient.instance.createTable(TableCreateRequest(number, capacity))
                if (response.isSuccessful) {
                    Toast.makeText(this@TableActivity, "Đã thêm bàn $number", Toast.LENGTH_SHORT).show()
                    loadTableList()
                }
            } catch (e: Exception) {
                Toast.makeText(this@TableActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateStatus(table: Table) {
        val newStatus = if (table.status == "Available") "Occupied" else "Available"
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitClient.instance.updateTableStatus(table.id, UpdateStatusRequest(newStatus))
                if (response.isSuccessful) loadTableList()
            } catch (e: Exception) {
                Toast.makeText(this@TableActivity, "Lỗi cập nhật", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
