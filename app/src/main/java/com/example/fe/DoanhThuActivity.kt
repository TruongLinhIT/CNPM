package com.example.fe

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.fe.model.DonHang
import java.text.NumberFormat
import java.util.*

class DoanhThuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doanh_thu)

        val tvTong = findViewById<TextView>(R.id.tvTongDoanhThu)

        // 1. Dữ liệu mẫu đơn hàng
        val danhSachDonHang = listOf(
            DonHang("DH001", "10:30 15/10", 150000.0),
            DonHang("DH002", "11:15 15/10", 250000.0),
            DonHang("DH003", "12:00 15/10", 100000.0)
        )

        // 2. Tính tổng tiền
        val tongTien = danhSachDonHang.sumOf { it.tongTien }

        // Định dạng tiền VNĐ cho đẹp
        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        tvTong.text = formatter.format(tongTien)
    }
}