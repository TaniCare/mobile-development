package com.dicoding.tanicare.data.remote

import com.dicoding.tanicare.data.model.PredictionResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PredictionApiService {
    @Multipart
    @POST("predict")
    fun predict(@Part file: MultipartBody.Part): Call<PredictionResponse>
}