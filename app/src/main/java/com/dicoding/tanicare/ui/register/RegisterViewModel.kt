package com.dicoding.tanicare.ui.register

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.tanicare.data.model.ApiResponse
import com.dicoding.tanicare.data.repository.RegisterRepository

class RegisterViewModel(context: Context) : ViewModel() {

    private val repository: RegisterRepository = RegisterRepository.getInstance(context)

    private val _registerResult = MutableLiveData<Result<ApiResponse>>()
    val registerResult: LiveData<Result<ApiResponse>> = _registerResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun register(name: String, email: String, password: String) {
        _isLoading.postValue(true)
        repository.register(name, email, password) { result ->
            _isLoading.postValue(false)
            _registerResult.postValue(result)
        }
    }
}
