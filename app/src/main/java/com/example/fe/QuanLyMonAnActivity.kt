package com.example.fe

import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.adapter.MonAnAdapter
import com.example.fe.model.*
import com.example.fe.network.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuanLyMonAnActivity : AppCompatActivity() {

    private lateinit var adapter: MonAnAdapter
    private val danhSachMonAn = mutableListOf<MonAn>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quan_ly_mon_an)

        // Cài đặt danh sách hiển thị
        val rv = findViewById<RecyclerView>(R.id.rvMonAn)
        adapter = MonAnAdapter(danhSachMonAn,
            onEditClick = { mon -> showDialogSua(mon) },
            onDeleteClick = { mon -> xoaMon(mon) }
        )
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        // Tải dữ liệu từ API
        loadDataFromApi()

        // Xử lý nút Thêm món
        findViewById<FloatingActionButton>(R.id.fabAddMonAn).setOnClickListener {
            showDialogThem()
        }
    }

    private fun loadDataFromApi() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getMenuItems()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val menuResponse = response.body()!!
                        if (menuResponse.success) {
                            val list = menuResponse.data.map { 
                                MonAn(it.item_id, it.name, it.price, it.image_url ?: "", it.description ?: "") 
                            }
                            adapter.updateData(list)
                        }
                    } else {
                        Toast.makeText(this@QuanLyMonAnActivity, "Không thể tải danh sách món ăn", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@QuanLyMonAnActivity, "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showDialogThem() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Thêm món mới")
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }
        val edtTen = EditText(this).apply { hint = "Tên món" }
        val edtGia = EditText(this).apply { hint = "Giá (VNĐ)"; inputType = android.text.InputType.TYPE_CLASS_NUMBER }
        val edtMoTa = EditText(this).apply { hint = "Mô tả" }
        
        layout.addView(edtTen)
        layout.addView(edtGia)
        layout.addView(edtMoTa)
        builder.setView(layout)

        builder.setPositiveButton("Lưu") { _, _ ->
            val ten = edtTen.text.toString()
            val gia = edtGia.text.toString().toDoubleOrNull() ?: 0.0
            val moTa = edtMoTa.text.toString()
            
            if (ten.isNotEmpty() && gia > 0) {
                themMonAn(MenuItemRequest(ten, gia, moTa))
            } else {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Hủy", null)
        builder.show()
    }

    private fun themMonAn(request: MenuItemRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.createMenuItem(request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@QuanLyMonAnActivity, "Thêm món thành công", Toast.LENGTH_SHORT).show()
                        loadDataFromApi()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@QuanLyMonAnActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showDialogSua(mon: MonAn) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sửa món ăn")
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }
        val edtTen = EditText(this).apply { 
            hint = "Tên món"
            setText(mon.tenMon)
        }
        val edtGia = EditText(this).apply { 
            hint = "Giá (VNĐ)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText(mon.gia.toInt().toString())
        }
        val edtMoTa = EditText(this).apply { 
            hint = "Mô tả"
            setText(mon.moTa)
        }
        
        layout.addView(edtTen)
        layout.addView(edtGia)
        layout.addView(edtMoTa)
        builder.setView(layout)

        builder.setPositiveButton("Cập nhật") { _, _ ->
            val ten = edtTen.text.toString()
            val gia = edtGia.text.toString().toDoubleOrNull() ?: 0.0
            val moTa = edtMoTa.text.toString()
            
            if (ten.isNotEmpty() && gia > 0) {
                capNhatMonAn(mon.id, MenuItemRequest(ten, gia, moTa))
            }
        }
        builder.setNegativeButton("Hủy", null)
        builder.show()
    }

    private fun capNhatMonAn(id: Int, request: MenuItemRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.updateMenuItem(id, request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@QuanLyMonAnActivity, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                        loadDataFromApi()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@QuanLyMonAnActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun xoaMon(mon: MonAn) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa món ${mon.tenMon}?")
            .setPositiveButton("Xóa") { _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = RetrofitClient.instance.deleteMenuItem(mon.id)
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@QuanLyMonAnActivity, "Đã xóa", Toast.LENGTH_SHORT).show()
                                loadDataFromApi()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@QuanLyMonAnActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}
