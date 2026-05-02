package com.example.fe

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.adapter.NhanVienAdapter
import com.example.fe.model.NhanVien
import com.example.fe.model.RegisterRequest
import com.example.fe.model.UpdateUserRequest
import com.example.fe.network.RetrofitClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuanLyNhanVienActivity : AppCompatActivity() {

    private lateinit var adapter: NhanVienAdapter
    private val danhSachNhanVien = mutableListOf<NhanVien>()
    private val roles = arrayOf("Manager", "Waitstaff", "Kitchen")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quan_ly_nhan_vien)

        // 1. Thiết lập RecyclerView
        val rv = findViewById<RecyclerView>(R.id.recyclerViewNhanVien)
        adapter = NhanVienAdapter(danhSachNhanVien,
            onEditClick = { nv -> showDialogSua(nv) },
            onDeleteClick = { nv -> xoaNhanVien(nv) }
        )
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        // 2. Tải dữ liệu từ API
        loadDataFromApi()

        // 3. Nút Thêm nhân viên
        findViewById<FloatingActionButton>(R.id.fabAddNhanVien).setOnClickListener {
            showDialogThem()
        }
    }

    private fun loadDataFromApi() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getAllUsers()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val userListResponse = response.body()!!
                        if (userListResponse.success) {
                            val list = userListResponse.data.map {
                                NhanVien(it.user_id, it.full_name, it.username, it.role)
                            }
                            adapter.updateData(list)
                        }
                    } else {
                        Toast.makeText(this@QuanLyNhanVienActivity, "Lỗi tải danh sách nhân viên", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@QuanLyNhanVienActivity, "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showDialogThem() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Thêm nhân viên mới")

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val edtTen = EditText(this).apply { hint = "Họ và tên" }
        val edtUser = EditText(this).apply { hint = "Tên đăng nhập" }
        val edtPass = EditText(this).apply { hint = "Mật khẩu"; inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD }
        
        val spnRole = Spinner(this)
        val roleAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnRole.adapter = roleAdapter

        layout.addView(edtTen)
        layout.addView(edtUser)
        layout.addView(edtPass)
        layout.addView(spnRole)
        builder.setView(layout)

        builder.setPositiveButton("Thêm") { _, _ ->
            val ten = edtTen.text.toString()
            val user = edtUser.text.toString()
            val pass = edtPass.text.toString()
            val role = spnRole.selectedItem.toString()

            if (ten.isNotEmpty() && user.isNotEmpty() && pass.isNotEmpty()) {
                themNhanVien(RegisterRequest(user, pass, ten, role))
            } else {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Hủy", null)
        builder.show()
    }

    private fun themNhanVien(request: RegisterRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.register(request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@QuanLyNhanVienActivity, "Thêm nhân viên thành công", Toast.LENGTH_SHORT).show()
                        loadDataFromApi()
                    } else {
                        Toast.makeText(this@QuanLyNhanVienActivity, "Lỗi: Tên đăng nhập có thể đã tồn tại", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@QuanLyNhanVienActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showDialogSua(nv: NhanVien) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sửa nhân viên")

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val edtTen = EditText(this).apply { 
            hint = "Họ và tên"
            setText(nv.hoTen)
        }
        val edtPass = EditText(this).apply { 
            hint = "Mật khẩu mới (để trống nếu không đổi)"
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        
        val spnRole = Spinner(this)
        val roleAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnRole.adapter = roleAdapter
        spnRole.setSelection(roles.indexOf(nv.role))

        layout.addView(edtTen)
        layout.addView(edtPass)
        layout.addView(spnRole)
        builder.setView(layout)

        builder.setPositiveButton("Cập nhật") { _, _ ->
            val ten = edtTen.text.toString()
            val pass = edtPass.text.toString().takeIf { it.isNotEmpty() }
            val role = spnRole.selectedItem.toString()

            if (ten.isNotEmpty()) {
                capNhatNhanVien(nv.id, UpdateUserRequest(ten, role, pass))
            }
        }
        builder.setNegativeButton("Hủy", null)
        builder.show()
    }

    private fun capNhatNhanVien(id: Int, request: UpdateUserRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.updateUser(id, request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@QuanLyNhanVienActivity, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                        loadDataFromApi()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@QuanLyNhanVienActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun xoaNhanVien(nv: NhanVien) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa nhân viên ${nv.hoTen}?")
            .setPositiveButton("Xóa") { _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = RetrofitClient.instance.deleteUser(nv.id)
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@QuanLyNhanVienActivity, "Đã xóa nhân viên", Toast.LENGTH_SHORT).show()
                                loadDataFromApi()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@QuanLyNhanVienActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}
