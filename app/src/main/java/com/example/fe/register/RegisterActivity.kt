package com.example.fe.register

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fe.R
import com.example.fe.login.LoginActivity
import com.example.fe.model.RegisterRequest
import com.example.fe.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private val roles = arrayOf("Manager", "Waitstaff", "Kitchen")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val edtFullName = findViewById<EditText>(R.id.edtFullName)
        val edtUsername = findViewById<EditText>(R.id.edtUsername)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val autoCompleteRole = findViewById<AutoCompleteTextView>(R.id.autoCompleteRole)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val txtLogin = findViewById<TextView>(R.id.txtLogin)

        // Setup Dropdown cho Role
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, roles)
        autoCompleteRole.setAdapter(adapter)

        btnRegister.setOnClickListener {
            val fullName = edtFullName.text.toString().trim()
            val username = edtUsername.text.toString().trim()
            val password = edtPassword.text.toString().trim()
            val role = autoCompleteRole.text.toString().trim()

            if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || role.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            performRegister(fullName, username, password, role)
        }

        txtLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun performRegister(name: String, user: String, pass: String, role: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = RegisterRequest(user, pass, name, role)
                val response = RetrofitClient.instance.register(request)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val registerResponse = response.body()!!
                        if (registerResponse.success) {
                            Toast.makeText(this@RegisterActivity, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@RegisterActivity, registerResponse.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@RegisterActivity, "Đăng ký thất bại", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Lỗi kết nối: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
