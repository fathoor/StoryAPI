package com.fathoor.storyapi.model.remote.retrofit

import com.fathoor.storyapi.model.remote.response.AuthResponse
import com.fathoor.storyapi.model.remote.response.Response
import com.fathoor.storyapi.model.remote.response.StoryDetailResponse
import com.fathoor.storyapi.model.remote.response.StoryListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun userRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<Response>

    @FormUrlEncoded
    @POST("login")
    fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<AuthResponse>

    @Multipart
    @POST("stories")
    fun userStory(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float? = null,
        @Part("lon") lon: Float? = null
    ) : Call<Response>

    @GET("stories")
    fun userStoryList(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ) : Call<StoryListResponse>

    @GET("stories")
    fun userStoryMap(
        @Header("Authorization") token: String,
        @Query("location") location: Int
    ) : Call<StoryListResponse>

    @GET("stories/{id}")
    fun userStoryDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ) : Call<StoryDetailResponse>
}