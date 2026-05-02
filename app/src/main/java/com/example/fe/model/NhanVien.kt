package com.example.fe.model

data class NhanVien(
    val id: Int,
    var hoTen: String,
    var tenDangNhap: String,
    var role: String,
    var matKhau: String? = null
)
