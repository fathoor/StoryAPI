package com.fathoor.storyapi.model.remote.retrofit

import com.fathoor.storyapi.model.remote.response.AuthResponse
import com.fathoor.storyapi.model.remote.response.Response
import com.fathoor.storyapi.model.remote.response.StoryDetailResponse
import com.fathoor.storyapi.model.remote.response.StoryListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @POST("/register")
    suspend fun userRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<Response>

    @POST("/login")
    suspend fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<AuthResponse>

    @Multipart
    @POST("/stories")
    suspend fun userStory(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody
    ) : Call<Response>

    @GET("/stories")
    suspend fun userStoryList(
        @Header("Authorization") token: String,
    ) : Call<StoryListResponse>

    @GET("/stories/{id}")
    suspend fun userStoryDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ) : Call<StoryDetailResponse>
}