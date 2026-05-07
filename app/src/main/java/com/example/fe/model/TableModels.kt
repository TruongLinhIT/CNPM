package com.example.fe.model

import com.google.gson.annotations.SerializedName

// --- TRẠNG THÁI ---
object OrderStatus {
    const val PENDING = "Pending"
    const val PREPARING = "Preparing"
    const val READY = "Ready"
    const val SERVED = "Served"
    const val PAID = "Paid"
    const val CANCELLED = "Cancelled"
}

// --- TABLE ---
data class Table(
    @SerializedName("table_id") val id: Int,
    @SerializedName("table_number") val number: Int,
    val capacity: Int,
    var status: String
)

data class TableCreateRequest(val table_number: Int, val capacity: Int)

data class TableListResponse(val success: Boolean, val message: String, val data: List<Table>)

data class TableResponse(val success: Boolean, val message: String, val data: Table?)

// --- CATEGORY ---
data class Category(val category_id: Int, val name: String)

data class CategoryRequest(val name: String)

data class CategoryListResponse(val success: Boolean, val message: String, val data: List<Category>)

data class CategoryResponse(val success: Boolean, val message: String, val data: Category?)

// --- MENU ---
data class MenuItemData(
    val item_id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val image_url: String?,
    val is_available: Boolean,
    @SerializedName("category_id") val category_id: Int?
)

data class MenuResponse(val success: Boolean, val message: String, val data: List<MenuItemData>)

data class MenuItemResponse(val success: Boolean, val message: String, val data: MenuItemData?)

data class MenuItemRequest(
    val name: String,
    val price: Double,
    val description: String = "",
    val category_id: Int = 1,
    val is_available: Boolean = true
)

// --- ORDER ---
data class OrderData(
    val order_id: Int,
    val user_id: Int,
    val table_id: Int,
    val subtotal: Double,
    val tax: Double,
    val discount: Double,
    val total_amount: Double,
    val status: String,
    val created_at: String,
    @SerializedName("User") val user: User?,
    @SerializedName("DiningTable") val diningTable: Table?, // Sử dụng diningTable (viết thường)
    @SerializedName("OrderDetails") val orderDetails: List<OrderDetailData>?
)

data class OrderDetailData(
    val order_detail_id: Int,
    val order_id: Int,
    val item_id: Int,
    val quantity: Int,
    val price_at_time: Double,
    var status: String, 
    @SerializedName("MenuItem") val menuItem: MenuItemData? // Sử dụng menuItem (viết thường)
)

data class CreateOrderRequest(
    val table_id: Int,
    val items: List<OrderItemRequest>,
    val user_id: Int? = null,
    val discount: Double = 0.0
)

data class OrderItemRequest(val item_id: Int, val quantity: Int, val notes: String? = "")

data class OrderListResponse(val success: Boolean, val message: String, val data: List<OrderData>)

data class OrderResponse(val success: Boolean, val message: String, val data: OrderData?)

data class UpdateStatusRequest(val status: String)

data class AddItemsRequest(val items: List<OrderItemRequest>)

// --- PAYMENT ---
data class PaymentRequest(val order_id: Int, val payment_method: String, val amount_paid: Double)

data class PaymentResponse(val success: Boolean, val message: String, val data: Any?)
