package com.example.fe.model

data class LoginRequest(val username: String, val password: String)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: LoginData?
)

data class LoginData(
    val user: User,
    val token: String? = null // Thêm token để xác thực request
)

data class RegisterRequest(
    val username: String,
    val password: String,
    val full_name: String,
    val role: String
)

data class RegisterResponse(val success: Boolean, val message: String, val data: User?)

data class UpdateUserRequest(val full_name: String, val role: String, val password: String? = null)

data class UsersListResponse(val success: Boolean, val message: String, val data: List<User>)

data class User(
    val user_id: Int,
    val username: String,
    val full_name: String,
    val role: String,
    val created_at: String? = null
)
