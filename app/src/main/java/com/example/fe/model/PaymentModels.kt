package com.example.fe.model

import com.google.gson.annotations.SerializedName

data class VnpayRequest(
    val order_id: Int,
    val amount: Double,
    val bankCode: String = ""
)

data class VnpayResponse(
    val success: Boolean,
    val message: String,
    val data: VnpayData?
)

data class VnpayData(
    val url: String
)
