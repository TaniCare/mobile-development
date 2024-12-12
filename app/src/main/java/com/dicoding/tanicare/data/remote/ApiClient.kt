package com.dicoding.tanicare.data.remote

import android.content.Context
import com.dicoding.tanicare.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://tanicare-application.et.r.appspot.com/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private fun authInterceptor(context: Context) = Interceptor { chain ->
        val accessToken = TokenManager.getAccessToken(context)

        val request = chain.request().newBuilder().apply {
            accessToken?.let {
                addHeader("Authorization", "Bearer $it")
            }
        }.build()

        chain.proceed(request)
    }

    private fun provideClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor(context))
            .build()
    }

    fun provideApiService(context: Context): ApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(provideClient(context))
            .build()
            .create(ApiService::class.java)
    }
}
