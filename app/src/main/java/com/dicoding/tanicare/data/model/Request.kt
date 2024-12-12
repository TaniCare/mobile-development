package com.dicoding.tanicare.data.model

data class ApiResponse(
    val status: Int,
    val message: String,
    val user_id: Int?
)

data class UserRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class LoginResponse(
    val status: Int,
    val access_token: String,
    val refresh_token: String
)



