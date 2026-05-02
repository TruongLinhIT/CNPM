package com.example.fe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fe.login.LoginActivity

class ManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manager)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnQuanLyBanAn = findViewById<Button>(R.id.btnQuanLyBanAn)
        val btnQuanLyDanhMuc = findViewById<Button>(R.id.btnQuanLyDanhMuc)
        val btnQuanLyMonAn = findViewById<Button>(R.id.btnQuanLyMonAn)
        val btnQuanLyNhanVien = findViewById<Button>(R.id.btnQuanLyNhanVien)
        val btnQuanLyDoanhThu = findViewById<Button>(R.id.btnQuanLyDoanhThu)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        btnQuanLyBanAn.setOnClickListener {
            val intent = Intent(this, TableActivity::class.java)
            intent.putExtra("USER_ROLE", "manager")
            startActivity(intent)
        }

        btnQuanLyDanhMuc.setOnClickListener {
            startActivity(Intent(this, QuanLyDanhMucActivity::class.java))
        }

        btnQuanLyMonAn.setOnClickListener {
            startActivity(Intent(this, QuanLyMonAnActivity::class.java))
        }

        btnQuanLyNhanVien.setOnClickListener {
            startActivity(Intent(this, QuanLyNhanVienActivity::class.java))
        }

        btnQuanLyDoanhThu.setOnClickListener {
            startActivity(Intent(this, DoanhThuActivity::class.java))
        }

        btnLogout.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
    }
}
