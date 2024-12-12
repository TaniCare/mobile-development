    package com.dicoding.tanicare.data.repository

    import android.content.Context
    import com.dicoding.tanicare.data.model.ApiResponse
    import com.dicoding.tanicare.data.model.RegisterRequest
    import com.dicoding.tanicare.data.remote.ApiClient
    import retrofit2.Call
    import retrofit2.Callback
    import retrofit2.Response

    class RegisterRepository private constructor(context: Context) {

        private val apiService = ApiClient.provideApiService(context)

        fun register(name: String, email: String, password: String, callback: (Result<ApiResponse>) -> Unit) {
            val request = RegisterRequest(name, email, password)
            apiService.register(request).enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            callback(Result.success(it))
                        } ?: run {
                            callback(Result.failure(Exception("Response body is null.")))
                        }
                    } else {
                        callback(Result.failure(Exception("${response.message()}")))
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    callback(Result.failure(Exception("${t.message}")))
                }
            })
        }

        companion object {
            @Volatile
            private var INSTANCE: RegisterRepository? = null

            fun getInstance(context: Context): RegisterRepository {
                return INSTANCE ?: synchronized(this) {
                    val instance = RegisterRepository(context)
                    INSTANCE = instance
                    instance
                }
            }
        }
    }
