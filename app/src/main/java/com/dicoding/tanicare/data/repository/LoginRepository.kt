package com.dicoding.tanicare.data.repository

import android.content.Context
import com.dicoding.tanicare.data.model.LoginResponse
import com.dicoding.tanicare.data.model.UserRequest
import com.dicoding.tanicare.data.remote.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository private constructor(context: Context) {

    private val apiService = ApiClient.provideApiService(context)

    fun login(userRequest: UserRequest, callback: (Result<LoginResponse>) -> Unit) {
        apiService.login(userRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        callback(Result.success(it))
                    } ?: run {
                        callback(Result.failure(Exception("Login response body is null.")))
                    }
                } else {
                    callback(Result.failure(Exception("${response.message()} Email or password is incorrect.")))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                callback(Result.failure(Exception("${t.message}")))
            }
        })
    }

    companion object {
        @Volatile
        private var INSTANCE: LoginRepository? = null

        fun getInstance(context: Context): LoginRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = LoginRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }
}
