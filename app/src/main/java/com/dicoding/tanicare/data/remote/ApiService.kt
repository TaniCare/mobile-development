package com.dicoding.tanicare.data.remote

import com.dicoding.tanicare.data.model.ApiResponse
import com.dicoding.tanicare.data.model.LoginResponse
import com.dicoding.tanicare.data.model.RegisterRequest
import com.dicoding.tanicare.data.model.UserRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    fun login(@Body request: UserRequest): Call<LoginResponse>

    @POST("signup")
    fun register(@Body request: RegisterRequest): Call<ApiResponse>
}
