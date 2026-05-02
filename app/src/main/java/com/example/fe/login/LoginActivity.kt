package com.example.fe.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fe.ManagerActivity
import com.example.fe.WaitstaffActivity
import com.example.fe.KitchenActivity
import com.example.fe.R
import com.example.fe.model.LoginRequest
import com.example.fe.network.RetrofitClient
import com.example.fe.register.RegisterActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val edtUsername = findViewById<EditText>(R.id.edtUsername)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val txtRegister = findViewById<TextView>(R.id.txtRegister)

        btnLogin.setOnClickListener {
            val username = edtUsername.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            performLogin(username, password)
        }

        txtRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun performLogin(user: String, pass: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Sử dụng đường dẫn đầy đủ để tránh Argument type mismatch
                val request = com.example.fe.model.LoginRequest(user, pass)
                val response = RetrofitClient.instance.login(request)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val loginResponse = response.body()!!

                        if (loginResponse.success && loginResponse.data != null) {
                            val userData = loginResponse.data!!.user
                            val userRole = userData.role
                            Toast.makeText(this@LoginActivity, "Chào mừng ${userData.full_name}!", Toast.LENGTH_SHORT).show()
                            navigateByRole(userRole)
                        } else {
                            Toast.makeText(this@LoginActivity, loginResponse.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Lỗi kết nối: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun navigateByRole(role: String?) {
        val intent = when (role?.lowercase()) {
            "admin", "manager" -> Intent(this, ManagerActivity::class.java)
            "waitstaff" -> Intent(this, WaitstaffActivity::class.java)
            "kitchen" -> Intent(this, KitchenActivity::class.java)
            else -> {
                Toast.makeText(this, "Quyền '$role' không được phép truy cập!", Toast.LENGTH_SHORT).show()
                return
            }
        }
        intent.putExtra("USER_ROLE", role)
        startActivity(intent)
        finish()
    }
}
