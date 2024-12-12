package com.dicoding.tanicare.data.repository

import android.content.Context
import com.dicoding.tanicare.data.local.AppDatabase
import com.dicoding.tanicare.data.local.HistoryEntity
import com.dicoding.tanicare.data.model.PredictionResponse
import com.dicoding.tanicare.data.remote.DetectionApiClient
import com.dicoding.tanicare.data.remote.PredictionApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ResultRepository private constructor(context: Context) {

    private val apiService = DetectionApiClient.createService(PredictionApiService::class.java)
    private val historyDao = AppDatabase.getInstance(context).historyDao()

    fun processImage(filePath: String, callback: (Result<PredictionResponse>) -> Unit) {
        val file = File(filePath)
        if (!file.exists() || file.length() == 0L) {
            callback(Result.failure(Exception("File not found or empty.")))
            return
        }

        val maxFileSize = 5 * 1024 * 1024 // 5 MB
        if (file.length() > maxFileSize) {
            callback(Result.failure(Exception("File is too large. Maximum size is 5 MB.")))
            return
        }

        val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)

        apiService.predict(filePart).enqueue(object : Callback<PredictionResponse> {
            override fun onResponse(
                call: Call<PredictionResponse>,
                response: Response<PredictionResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        callback(Result.success(it))
                    } ?: run {
                        callback(Result.failure(Exception("Response is empty.")))
                    }
                } else {
                    callback(Result.failure(Exception("Server error: ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<PredictionResponse>, t: Throwable) {
                callback(Result.failure(Exception("Network error: ${t.message}")))
            }
        })
    }

    suspend fun saveHistory(history: HistoryEntity) {
        withContext(Dispatchers.IO) {
            historyDao.insertHistory(history)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ResultRepository? = null

        fun getInstance(context: Context): ResultRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = ResultRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }
}

