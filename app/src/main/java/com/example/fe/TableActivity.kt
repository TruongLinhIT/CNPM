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
import com.example.fe.model.Table
import com.example.fe.model.TableCreateRequest
import com.example.fe.network.RetrofitClient
import kotlinx.coroutines.*

class TableActivity : AppCompatActivity() {
    private lateinit var rvTables: RecyclerView
    private lateinit var adapter: TableAdapter
    private var userRole: String? = null
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table)

        userRole = intent.getStringExtra("USER_ROLE")?.lowercase()
        token = intent.getStringExtra("AUTH_TOKEN")

        rvTables = findViewById(R.id.rvTables)
        val fabAdd = findViewById<View>(R.id.fabAddTable)

        // Phân quyền: Chỉ Manager mới thấy nút thêm bàn
        if (userRole == "manager") {
            fabAdd.visibility = View.VISIBLE
            fabAdd.setOnClickListener {
                showAddTableDialog()
            }
        } else {
            fabAdd.visibility = View.GONE
        }

        setupRecyclerView()
        loadTableList()
    }

    private fun setupRecyclerView() {
        adapter = TableAdapter(emptyList()) { table ->
            updateStatus(table)
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
                } else {
                    Toast.makeText(this@TableActivity, "Không thể tải danh sách bàn", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@TableActivity, "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
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
                val number = edtNumber.text.toString().toIntOrNull()
                val capacity = edtCapacity.text.toString().toIntOrNull()

                if (number != null && capacity != null) {
                    createNewTable(number, capacity)
                } else {
                    Toast.makeText(this, "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun createNewTable(number: Int, capacity: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    // Gọi API thêm bàn mới
                    RetrofitClient.instance.createTable(TableCreateRequest(number, capacity))
                }
                if (response.isSuccessful) {
                    Toast.makeText(this@TableActivity, "Thêm bàn $number thành công", Toast.LENGTH_SHORT).show()
                    loadTableList() // Reload danh sách bàn
                } else {
                    Toast.makeText(this@TableActivity, "Lỗi: Số bàn đã tồn tại hoặc dữ liệu sai", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@TableActivity, "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateStatus(table: Table) {
        val newStatus = if (table.status == "Available") "Occupied" else "Available"
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.updateTableStatus(table.id, mapOf("status" to newStatus))
                }
                if (response.isSuccessful) {
                    loadTableList()
                }
            } catch (e: Exception) {
                Toast.makeText(this@TableActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
