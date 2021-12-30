package com.example.android.marsphotos.network

import com.example.android.marsphotos.network.URL.BASE_URL
import com.reglardo.audiogram.network.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

object URL {
    const val BASE_URL =
        "http://10.0.2.2"
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

// ------------- Authentication
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
    ): GeneralResponse

    @POST("user/login/")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
    ): GeneralResponse
}

object AuthenticationApi {
    val retrofitService : AuthenticationApiService by lazy {
        retrofit.create(AuthenticationApiService::class.java)
    }
}

// ------------- User
interface UserApiService {

    @POST("user/info/")
    @FormUrlEncoded
    suspend fun getInfo(
        @Field("token") token: String,
        @Field("username") username: String
    ): InfoResponse

    @POST("user/info/")
    @FormUrlEncoded
    suspend fun getMyInfo(
        @Field("token") token: String,
    ): InfoResponse

    @POST("user/search/")
    @FormUrlEncoded
    suspend fun searchUsers(
        @Field("token") token: String,
        @Field("username") username: String,
    ): SearchResponse

    @POST("user/upload_profile_image/")
    @Multipart
    suspend fun uploadImage(
        @Part("token") token: String,
        @Part image: MultipartBody.Part,
    ): GeneralResponse

    @POST("voice/upload/")
    @Multipart
    suspend fun uploadVoice(
        @Part("token") token: String,
        @Part voice: MultipartBody.Part,
    ): GeneralResponse

    @POST("user/follow/")
    @FormUrlEncoded
    suspend fun followUser(
        @Field("token") token: String,
        @Field("username") username: String,
    ): GeneralResponse

    @POST("user/unfollow/")
    @FormUrlEncoded
    suspend fun unfollowUser(
        @Field("token") token: String,
        @Field("username") username: String,
    ): GeneralResponse
}

object UserApi{
    val retrofitService : UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }
}


// ------------- Voice
interface VoiceApiService {

    @POST("voice/get_profile_voices/")
    @FormUrlEncoded
    suspend fun getProfileVoices(
        @Field("token") token: String,
        @Field("username") username: String?
    ): VoiceResponse

    @POST("voice/get_home_voices/")
    @FormUrlEncoded
    suspend fun getHomeVoices(
        @Field("token") token: String,
    ): VoiceResponse

    @POST("voice/like/")
    @FormUrlEncoded
    suspend fun like(
        @Field("token") token: String,
        @Field("voiceId") voiceId: String,
    ): VoiceResponse

    @POST("voice/comment/")
    @FormUrlEncoded
    suspend fun comment(
        @Field("token") token: String,
        @Field("voiceId") voiceId: String,
        @Field("comment") comment: String,
    ): VoiceResponse

    @POST("voice/get_comments/")
    @FormUrlEncoded
    suspend fun getComments(
        @Field("token") token: String,
        @Field("voiceId") voiceId: String,
    ): CommentResponse
}

object VoiceApi{
    val retrofitService : VoiceApiService by lazy {
        retrofit.create(VoiceApiService::class.java)
    }
}
