package com.example.fe.network

import com.example.fe.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- MODULE 1 & 2: AUTH & USERS ---
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/users")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("api/users")
    suspend fun getAllUsers(): Response<UsersListResponse>

    @PUT("api/users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body request: UpdateUserRequest): Response<RegisterResponse>

    @DELETE("api/users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<RegisterResponse>

    // --- MODULE 3: TABLES ---
    @GET("api/tables")
    suspend fun getTables(): Response<TableListResponse>

    @POST("api/tables")
    suspend fun createTable(@Body request: TableCreateRequest): Response<TableResponse>

    @PUT("api/tables/{id}/status")
    suspend fun updateTableStatus(@Path("id") id: Int, @Body status: UpdateStatusRequest): Response<TableResponse>

    // --- MODULE 4: MENU ---
    @GET("api/categories")
    suspend fun getCategories(): Response<CategoryListResponse>

    @POST("api/categories")
    suspend fun createCategory(@Body request: CategoryRequest): Response<CategoryResponse>

    @PUT("api/categories/{id}")
    suspend fun updateCategory(@Path("id") id: Int, @Body request: CategoryRequest): Response<CategoryResponse>

    @DELETE("api/categories/{id}")
    suspend fun deleteCategory(@Path("id") id: Int): Response<CategoryResponse>

    @GET("api/menu")
    suspend fun getMenuItems(): Response<MenuResponse>

    @POST("api/menu")
    suspend fun createMenuItem(@Body request: MenuItemRequest): Response<MenuItemResponse>

    @PUT("api/menu/{id}")
    suspend fun updateMenuItem(@Path("id") id: Int, @Body request: MenuItemRequest): Response<MenuItemResponse>

    @DELETE("api/menu/{id}")
    suspend fun deleteMenuItem(@Path("id") id: Int): Response<MenuItemResponse>

    // --- MODULE 5: ORDERS ---
    @POST("api/orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<OrderResponse>

    @GET("api/orders")
    suspend fun getOrders(): Response<OrderListResponse>

    @GET("api/orders/active")
    suspend fun getActiveOrders(): Response<OrderListResponse>

    @GET("api/orders/{id}")
    suspend fun getOrderById(@Path("id") id: Int): Response<OrderResponse>
    
    @PUT("api/orders/{id}/status")
    suspend fun updateOrderStatus(@Path("id") id: Int, @Body status: UpdateStatusRequest): Response<OrderResponse>

    @PUT("api/orders/items/{detailId}/status")
    suspend fun updateOrderDetailStatus(@Path("detailId") detailId: Int, @Body status: UpdateStatusRequest): Response<OrderResponse>

    @POST("api/orders/{id}/items")
    suspend fun addOrderItems(@Path("id") id: Int, @Body request: AddItemsRequest): Response<OrderResponse>

    // --- MODULE 6: PAYMENTS & REPORTS ---
    @POST("api/payments")
    suspend fun createPayment(@Body request: PaymentRequest): Response<PaymentResponse>

    @GET("api/reports/revenue")
    suspend fun getRevenueReport(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null
    ): Response<RevenueResponse>
}
