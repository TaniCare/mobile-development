package com.dicoding.tanicare.ui.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.tanicare.data.model.LoginResponse
import com.dicoding.tanicare.data.model.UserRequest
import com.dicoding.tanicare.data.repository.LoginRepository

class LoginViewModel(context: Context) : ViewModel() {

    private val repository: LoginRepository = LoginRepository.getInstance(context)

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    fun login(email: String, password: String) {
        val userRequest = UserRequest(email, password)
        repository.login(userRequest) { result ->
            _loginResult.postValue(result)
        }
    }
}
