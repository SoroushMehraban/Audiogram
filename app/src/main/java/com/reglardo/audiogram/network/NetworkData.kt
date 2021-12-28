package com.reglardo.audiogram.network

import com.google.gson.annotations.SerializedName

data class Response (
    val success: Boolean,
    val message: String
)

data class SignUpData (
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("password_confirmation") val passwordConfirmation: String,
)

data class LoginData(
    val email: String,
    val password: String
)