package com.example.android.marsphotos.network

import com.reglardo.audiogram.network.Response
import com.reglardo.audiogram.network.SignUpData
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val BASE_URL =
    "http://10.0.2.2/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface AuthenticationApiService {

    @POST("user/sign_up/")
    @FormUrlEncoded
    suspend fun signUp(
        @Field("first_name") firstName: String,
        @Field("last_name") lastName: String,
        @Field("email") email: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirmation: String,
    ): Response

    @POST("user/login/")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
    ): Response
}

object AuthenticationApi {
    val retrofitService : AuthenticationApiService by lazy {
        retrofit.create(AuthenticationApiService::class.java)
    }
}