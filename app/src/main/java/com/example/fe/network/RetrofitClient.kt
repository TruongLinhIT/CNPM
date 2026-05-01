package com.example.fe.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // 1.NẾU DÙNG MÁY ẢO: Dùng "http://10.0.2.2:3000/"
    // 2. NẾU DÙNG MÁY THẬT: Dùng IP máy tính, ví dụ "http://192.168.1.5:3000/"
    private const val BASE_URL = "http://192.168.1.11:3000/"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }
}
