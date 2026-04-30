package com.example.fe.model

/**
 * 1. Login Models
 */
data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: LoginData?
)

data class LoginData(
    val user: User
)

/**
 * 2. Register Models (Khớp với BE POST /api/users)
 */
data class RegisterRequest(
    val username: String,
    val password: String,
    val full_name: String,
    val role: String
)

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val data: User? // BE trả về thông tin user sau khi tạo
)

/**
 * 3. Common User Model
 */
data class User(
    val user_id: Int,
    val username: String,
    val full_name: String,
    val role: String,
    val created_at: String? = null
)
