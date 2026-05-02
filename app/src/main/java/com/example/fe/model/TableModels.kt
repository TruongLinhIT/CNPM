package com.example.fe.model

import com.google.gson.annotations.SerializedName

// --- TABLE MODELS ---
data class Table(
    @SerializedName("table_id") val id: Int,
    @SerializedName("table_number") val number: Int,
    val capacity: Int,
    var status: String // "Available", "Occupied"
)

data class TableCreateRequest(
    val table_number: Int,
    val capacity: Int
)

data class TableListResponse(
    val success: Boolean,
    val message: String,
    val data: List<Table>
)

data class TableResponse(
    val success: Boolean,
    val message: String,
    val data: Table?
)

// --- CATEGORY MODELS ---
data class Category(
    val category_id: Int,
    val name: String
)

data class CategoryRequest(
    val name: String
)

data class CategoryListResponse(
    val success: Boolean,
    val message: String,
    val data: List<Category>
)

data class CategoryResponse(
    val success: Boolean,
    val message: String,
    val data: Category?
)

// --- MENU MODELS ---
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
    val category_id: Int = 1,
    val is_available: Boolean = true
)

// --- ORDER MODELS ---
data class CreateOrderRequest(
    val table_id: Int,
    val items: List<OrderItemRequest>
)

data class OrderItemRequest(
    val item_id: Int,
    val quantity: Int,
    val notes: String? = ""
)

data class OrderListResponse(
    val success: Boolean,
    val message: String,
    val data: List<OrderData>
)

data class OrderResponse(
    val success: Boolean,
    val message: String,
    val data: OrderData?
)

data class OrderData(
    val order_id: Int,
    val table_id: Int,
    val total_price: Double,
    val status: String,
    val created_at: String
)

// --- USER MODELS ---
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
