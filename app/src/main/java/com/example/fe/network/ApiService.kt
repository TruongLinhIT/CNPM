package com.example.fe.network

import com.example.fe.model.LoginRequest
import com.example.fe.model.LoginResponse
import com.example.fe.model.RegisterRequest
import com.example.fe.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    // 1. Đăng nhập
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    // 2. Đăng ký (Tạo user mới) - Khớp với router.post('/', createUser) trong user.routes.js
    @POST("api/users")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>
}
