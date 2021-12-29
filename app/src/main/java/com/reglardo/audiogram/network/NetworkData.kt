package com.reglardo.audiogram.network

import com.google.gson.annotations.SerializedName

data class AuthenticationResponse (
    val success: Boolean,
    val message: String
)

data class InfoResponse (
    val success: Boolean,
    val message: String?,
    val username: String?,
    val firstName: String?,
    val lastName: String?,
    val image: String?,
    val followers: Int?,
    val followings: Int?,
    val voices: Int?,
)

data class SearchResponse (
    val success: Boolean,
    val message: String?,
    val users: List<UserSearchResponse>?
)

data class UserSearchResponse(
    val username: String,
    val firstName: String,
    val lastName: String
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
    val username: String,
    val password: String
)