package com.example.fe.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // 1. NẾU DÙNG MÁY ẢO: Dùng "http://10.0.2.2:3000/"
    // 2. NẾU DÙNG MÁY THẬT: Dùng IP máy tính, ví dụ "http://192.168.1.5:3000/"
    private const val BASE_URL = "http://10.0.2.2:3000/"

    // Biến lưu trữ Token sau khi đăng nhập thành công
    var authToken: String? = null

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
            
            // Tự động thêm Header Authorization nếu có Token
            authToken?.let {
                requestBuilder.addHeader("Authorization", "Bearer $it")
            }
            
            val request = requestBuilder.build()
            chain.proceed(request)
        }
        .build()

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }
}
