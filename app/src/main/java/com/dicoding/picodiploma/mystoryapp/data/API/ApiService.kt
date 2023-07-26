package com.dicoding.picodiploma.mystoryapp.data.API

import com.dicoding.picodiploma.mystoryapp.data.response.AddNewStoryResponse
import com.dicoding.picodiploma.mystoryapp.data.response.LoginResponse
import com.dicoding.picodiploma.mystoryapp.data.response.RegisterResponse
import com.dicoding.picodiploma.mystoryapp.data.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
/*
    @GET("stories")
    fun getListStory(
        @Header("Authorization") bearer: String?,
    ): retrofit2.Call<StoryResponse>
 */

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") bearer: String?,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): StoryResponse

    @Multipart
    @POST("stories")
    fun postNewStory(
        @Header("Authorization") bearer: String?,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody?,
    ): retrofit2.Call<AddNewStoryResponse>

    @FormUrlEncoded
    @POST("register")
    fun Register(
        @Field("name") name: String?,
        @Field("email") email: String?,
        @Field("password") password: String?
    ): retrofit2.Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun Login(
        @Field("email") email: String?,
        @Field("password") password: String?
    ): retrofit2.Call<LoginResponse>

    @GET("stories")
    fun getStoriesWithLocation(
        @Header("Authorization") bearer: String?,
        @Query("location") location: Int
    ) : Call<StoryResponse>
}