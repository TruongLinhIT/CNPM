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
import com.example.fe.model.*
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

        val rv = findViewById<RecyclerView>(R.id.recyclerViewNhanVien)
        adapter = NhanVienAdapter(danhSachNhanVien,
            onEditClick = { nv -> showDialogSua(nv) },
            onDeleteClick = { nv -> xoaNhanVien(nv) }
        )
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        loadDataFromApi()

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
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@QuanLyNhanVienActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
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
        spnRole.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)

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
            }
        }
        builder.setNegativeButton("Hủy", null)
        builder.show()
    }

    private fun themNhanVien(request: RegisterRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // SỬA TẠI ĐÂY: register -> registerUser
                val response = RetrofitClient.instance.registerUser(request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@QuanLyNhanVienActivity, "Thành công", Toast.LENGTH_SHORT).show()
                        loadDataFromApi()
                    }
                }
            } catch (e: Exception) { /* Log error */ }
        }
    }

    private fun showDialogSua(nv: NhanVien) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sửa nhân viên")
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }
        val edtTen = EditText(this).apply { setText(nv.hoTen) }
        val edtPass = EditText(this).apply { hint = "Mật khẩu mới (nếu có)"; inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD }
        val spnRole = Spinner(this)
        spnRole.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)
        spnRole.setSelection(roles.indexOf(nv.role))

        layout.addView(edtTen)
        layout.addView(edtPass)
        layout.addView(spnRole)
        builder.setView(layout)

        builder.setPositiveButton("Cập nhật") { _, _ ->
            val ten = edtTen.text.toString()
            val pass = edtPass.text.toString().takeIf { it.isNotEmpty() }
            val role = spnRole.selectedItem.toString()
            capNhatNhanVien(nv.id, UpdateUserRequest(ten, role, pass))
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
                        Toast.makeText(this@QuanLyNhanVienActivity, "Đã cập nhật", Toast.LENGTH_SHORT).show()
                        loadDataFromApi()
                    }
                }
            } catch (e: Exception) { /* Log error */ }
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
                            if (response.isSuccessful) loadDataFromApi()
                        }
                    } catch (e: Exception) { /* Log error */ }
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}
