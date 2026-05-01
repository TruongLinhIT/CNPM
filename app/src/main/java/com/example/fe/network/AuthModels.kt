package com.example.fe.network

// Dữ liệu gửi lên
data class LoginRequest(
    val username: String,
    val password: String
)

// Dữ liệu Server trả về
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val role: String?, // "manager", "waitstaff", v.v.
    val token: String?
)
