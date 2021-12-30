package com.reglardo.audiogram.network

import com.google.gson.annotations.SerializedName

data class GeneralResponse (
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
    val isMe: Boolean?,
    val isFollowed: Boolean?
)

data class VoiceResponse(
    val success: Boolean,
    val message: String?,
    val voices: List<UserVoiceResponse>?
)

data class UserVoiceResponse(
    val id: String,
    val username: String,
    val imageUrl: String,
    val voiceUrl: String,
    val publishDate: String,
    val likeNumbers: Int,
    val commentNumbers: Int,
    val isLiked: Boolean
)

data class CommentResponse(
    val success: Boolean,
    val message: String?,
    val comments: List<VoiceCommentResponse>?
)

data class VoiceCommentResponse(
    val userImage: String,
    val username: String,
    val commentDate: String,
    val content: String,
)

data class SearchResponse (
    val success: Boolean,
    val message: String?,
    val users: List<UserSearchResponse>?
)

data class UserSearchResponse(
    val username: String,
    val image: String,
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