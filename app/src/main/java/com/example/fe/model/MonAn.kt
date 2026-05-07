package com.example.fe.model

data class MonAn(
    val id: Int,
    val tenMon: String,
    val gia: Double,
    val hinhAnh: String = "",
    val moTa: String = "",
    val category_id: Int = 0
)
