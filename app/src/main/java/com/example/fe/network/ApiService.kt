package com.example.fe.network

import com.example.fe.model.LoginRequest
import com.example.fe.model.LoginResponse
import com.example.fe.model.MonAn
import com.example.fe.model.RegisterRequest
import com.example.fe.model.RegisterResponse
import com.example.fe.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    // 1. Đăng nhập
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    // 2. Đăng ký (Tạo user mới)
    @POST("api/users")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    // 3. Lấy danh sách món ăn
    @GET("api/menu")
    suspend fun getMenuItems(): Response<MenuResponse>

    // 4. Thêm món ăn mới
    @POST("api/menu")
    suspend fun createMenuItem(@Body request: MenuItemRequest): Response<MenuItemResponse>

    // 5. Cập nhật món ăn
    @PUT("api/menu/{id}")
    suspend fun updateMenuItem(@Path("id") id: Int, @Body request: MenuItemRequest): Response<MenuItemResponse>

    // 6. Xóa món ăn
    @DELETE("api/menu/{id}")
    suspend fun deleteMenuItem(@Path("id") id: Int): Response<MenuItemResponse>

    // 7. Lấy danh sách nhân viên
    @GET("api/users")
    suspend fun getAllUsers(): Response<UsersListResponse>

    // 8. Cập nhật nhân viên
    @PUT("api/users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body request: UpdateUserRequest): Response<RegisterResponse>

    // 9. Xóa nhân viên
    @DELETE("api/users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<RegisterResponse>
}

// Wrapper cho response từ BE
data class MenuResponse(
    val success: Boolean,
    val message: String,
    val data: List<MenuItemData>
)

data class MenuItemResponse(
    val success: Boolean,
    val message: String,
    val data: MenuItemData?
)

data class MenuItemData(
    val item_id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val image_url: String?,
    val is_available: Boolean,
    val category_id: Int?
)

data class MenuItemRequest(
    val name: String,
    val price: Double,
    val description: String = "",
    val category_id: Int = 1, // Mặc định
    val is_available: Boolean = true
)

data class UsersListResponse(
    val success: Boolean,
    val message: String,
    val data: List<User>
)

data class UpdateUserRequest(
    val full_name: String,
    val role: String,
    val password: String? = null
)
