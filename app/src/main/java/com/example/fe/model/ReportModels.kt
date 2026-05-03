package com.example.fe.model

data class RevenueResponse(
    val success: Boolean,
    val message: String,
    val data: RevenueData
)

data class RevenueData(
    val total_revenue: Double,
    val paid_orders: Int,
    val from: String?,
    val to: String?
)
